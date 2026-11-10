package kr.bjj_oss.profile.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import kr.bjj_oss.profile.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/** Only cache URIs and crop coordinates survive recreation; bitmap memory stays at the UI boundary. */
@Composable
fun ProfileCropDialog(uri: String, onCancel: () -> Unit, onCropped: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var error by remember(uri) { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    var zoom by rememberSaveable(uri) { mutableFloatStateOf(1f) }
    var x by rememberSaveable(uri) { mutableFloatStateOf(0.5f) }
    var y by rememberSaveable(uri) { mutableFloatStateOf(0.5f) }
    LaunchedEffect(uri, retry) {
        error = false
        try {
            bitmap = withContext(Dispatchers.IO) {
                val source = Uri.parse(uri)
                if (Build.VERSION.SDK_INT >= 28) ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, source)) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    val ratio = minOf(1f, 2048f / maxOf(info.size.width, info.size.height))
                    decoder.setTargetSize((info.size.width * ratio).roundToInt().coerceAtLeast(1), (info.size.height * ratio).roundToInt().coerceAtLeast(1))
                } else {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(source)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 1
                        while (maxOf(bounds.outWidth, bounds.outHeight) / inSampleSize > 2048) inSampleSize *= 2
                    }
                    val decoded = requireNotNull(context.contentResolver.openInputStream(source)?.use { BitmapFactory.decodeStream(it, null, options) })
                    val orientation = context.contentResolver.openInputStream(source)?.use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1) }
                    val matrix = Matrix().apply {
                        when (orientation) {
                            2 -> setScale(-1f, 1f); 3 -> setRotate(180f); 4 -> setScale(1f, -1f)
                            5 -> { setRotate(90f); postScale(-1f, 1f) }; 6 -> setRotate(90f)
                            7 -> { setRotate(-90f); postScale(-1f, 1f) }; 8 -> setRotate(-90f)
                        }
                    }
                    Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true).also { if (it !== decoded) decoded.recycle() }
                }
            }
        } catch (cancel: CancellationException) { throw cancel }
        catch (_: Exception) { error = true }
    }
    DisposableEffect(bitmap) { val current = bitmap; onDispose { current?.recycle() } }
    Dialog(onDismissRequest = { if (!saving) onCancel() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing).imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.profile_image_crop), style = MaterialTheme.typography.titleLarge)
                val source = bitmap
                if (source != null) {
                    Canvas(Modifier.widthIn(max = 420.dp).fillMaxWidth().aspectRatio(1f).pointerInput(saving) {
                        if (!saving) detectDragGestures { change, drag ->
                            change.consume(); x = (x - drag.x / size.width).coerceIn(0f, 1f); y = (y - drag.y / size.height).coerceIn(0f, 1f)
                        }
                    }) {
                        val side = (minOf(source.width, source.height) / zoom).toInt().coerceAtLeast(1)
                        drawImage(source.asImageBitmap(), srcOffset = IntOffset(((source.width - side) * x).roundToInt(), ((source.height - side) * y).roundToInt()),
                            srcSize = IntSize(side, side), dstSize = IntSize(size.width.toInt(), size.height.toInt()))
                    }
                    Text(stringResource(R.string.profile_crop_zoom)); Slider(zoom, { zoom = it }, valueRange = 1f..4f, enabled = !saving)
                    Text(stringResource(R.string.profile_crop_horizontal)); Slider(x, { x = it }, enabled = !saving)
                    Text(stringResource(R.string.profile_crop_vertical)); Slider(y, { y = it }, enabled = !saving)
                    ProfileSubmit(stringResource(R.string.profile_confirm), !saving) {
                        saving = true
                        scope.launch {
                            try {
                                val output = withContext(Dispatchers.IO) {
                                    val side = (minOf(source.width, source.height) / zoom).toInt().coerceAtLeast(1)
                                    val cropped = Bitmap.createBitmap(source, ((source.width - side) * x).roundToInt(), ((source.height - side) * y).roundToInt(), side, side)
                                    val folder = File(context.cacheDir, "profile_images").apply { mkdirs() }
                                    val file = File.createTempFile("crop_", ".jpg", folder)
                                    try { file.outputStream().use { check(cropped.compress(Bitmap.CompressFormat.JPEG, 90, it)) } }
                                    finally { if (cropped !== source) cropped.recycle() }
                                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file).toString()
                                }
                                onCropped(output)
                            } catch (cancel: CancellationException) { throw cancel }
                            catch (_: Exception) { error = true }
                            finally { saving = false }
                        }
                    }
                } else if (!error) CircularProgressIndicator()
                if (error) {
                    Text(stringResource(R.string.profile_image_upload_error), color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { retry++ }) { Text(stringResource(R.string.profile_retry)) }
                }
                TextButton(onClick = onCancel, enabled = !saving) { Text(stringResource(R.string.profile_cancel)) }
            }
        }
    }
}
