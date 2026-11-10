package kr.bjj_oss.profile.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.components.*
import kr.bjj_oss.profile.model.*
import kr.bjj_oss.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class CompetitionEditorDestination(val originalIndex: Int = -1, val groupKey: String? = null)

@Serializable
data object ProfileCommunityDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    onAcademyClick: (String) -> Unit = {},
    onMyStyleClick: (String) -> Unit = {},
    onCompetitionClick: () -> Unit = {},
    onNicknameClick: (String) -> Unit = {},
    onEditCompetition: (Int, String?) -> Unit = { _, _ -> },
    onSettingsClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onHeaderAppearanceChanged: (Boolean) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
) {
    val model = hiltViewModel<ProfileViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, model) {
        val observer =
            LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) model.refresh() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    var sheet by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedGroup by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteGroup by rememberSaveable { mutableStateOf<String?>(null) }
    var verification by rememberSaveable { mutableStateOf(false) }
    var cameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    var cropUri by rememberSaveable { mutableStateOf<String?>(null) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var closingSheet by remember { mutableStateOf(false) }

    fun afterSheet(action: () -> Unit) {
        if (closingSheet) return
        closingSheet = true
        scope.launch {
            try {
                sheetState.hide(); sheet = null; action()
            } finally {
                closingSheet = false
            }
        }
    }

    LaunchedEffect(model) {
        model.events.collect { message ->
            if (message in listOf(
                    R.string.profile_image_upload_error,
                    R.string.profile_image_save_error,
                    R.string.profile_image_deleted
                )
            ) preview = null
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(state.user?.id) { preview = null }
    LaunchedEffect(state.isGuest) {
        if (state.isGuest) {
            sheet = null; cropUri = null; preview = null
        }
    }

    fun selected(uri: Uri?) {
        if (uri != null && !state.isGuest && state.user != null) {
            if (verification) model.uploadImage(uri.toString(), true) else cropUri = uri.toString()
        } else verification = false
    }

    val gallery =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { selected(it) }

    val camera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            selected(if (success) cameraUri?.let(Uri::parse) else null); cameraUri = null
        }

    fun launchCamera() {
        try {
            val folder = File(context.cacheDir, "profile_images").apply { mkdirs() }
            val file = File.createTempFile("profile_", ".jpg", folder)
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            cameraUri = uri.toString(); camera.launch(uri)
        } catch (_: Exception) {
            Toast.makeText(context, R.string.profile_image_upload_error, Toast.LENGTH_SHORT).show()
        }
    }

    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) launchCamera() else {
                verification = false; Toast.makeText(
                    context,
                    R.string.profile_camera_permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    MyProfileContent(
        state, padding, modifier, preview,
        onNickname = { onNicknameClick(state.nickname) },
        onAcademy = { onAcademyClick(state.community?.academyName.orEmpty()) },
        onBelt = { sheet = "belt" },
        onStyle = onMyStyleClick,
        onCompetition = onCompetitionClick,
        onGroup = { selectedGroup = it; sheet = "competition" },
        onImage = { verification = false; sheet = "image" },
        onVerification = { sheet = "verification" },
        onSettings = onSettingsClick,
        onCommunity = onCommunityClick,
        onLogin = onLoginClick,
        onRetry = { model.refresh(initial = true) },
        onMove = model::moveGroup,
        onCancelOrder = model::cancelOrder,
        onSaveOrder = model::saveOrder,
        onBeginOrder = model::beginOrder,
        onChrome = onHeaderAppearanceChanged,
        onBack = onBackClick
    )

    if (sheet != null) key(sheet) {
        ModalBottomSheet(
            onDismissRequest = { sheet = null },
            sheetState = sheetState,
            sheetMaxWidth = 600.dp
        ) {
            when (sheet) {
                "belt" -> BeltPickerSheet(
                    state.community?.beltRank,
                    state.community?.beltStripe
                ) { rank, stripe -> afterSheet { model.changeBelt(rank, stripe) } }

                "image" -> Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.profile_image_options),
                        style = MaterialTheme.typography.titleLarge
                    )
                    ProfileSubmit(stringResource(R.string.profile_image_camera), !state.busy) {
                        afterSheet {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) launchCamera()
                            else permission.launch(Manifest.permission.CAMERA)
                        }
                    }
                    ProfileSubmit(stringResource(R.string.profile_image_gallery), !state.busy) {
                        afterSheet {
                            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                    if (!verification && state.user?.profileImageUrl?.let {
                            it.startsWith("http://") || it.startsWith(
                                "https://"
                            )
                        } == true)
                        TextButton(
                            onClick = { afterSheet { preview = null; model.deleteImage() } },
                            enabled = !state.busy
                        ) { Text(stringResource(R.string.profile_image_delete)) }
                    TextButton(onClick = { afterSheet { verification = false } }) {
                        Text(
                            stringResource(R.string.profile_cancel)
                        )
                    }
                }

                "verification" -> VerificationSheet(state) { upload ->
                    if (upload) afterSheet {
                        verification = true; sheet = "image"
                    } else afterSheet {}
                }

                "competition" -> {
                    val group = state.community?.competitions.orEmpty().competitionGroups()
                        .find { it.key == selectedGroup }
                    if (group != null) Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 630.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(competitionTitle(group), style = MaterialTheme.typography.titleLarge)
                        ProfileSubmit(
                            stringResource(if (group.pinned) R.string.profile_unpin else R.string.profile_pin),
                            !state.busy
                        ) { afterSheet { model.togglePin(group.key) } }
                        group.entries.forEach { entry ->
                            ProfileSubmit(
                                stringResource(
                                    R.string.profile_division_edit,
                                    divisionLabel(entry.value)
                                ), !state.busy
                            ) { afterSheet { onEditCompetition(entry.index, null) } }
                        }
                        ProfileSubmit(
                            stringResource(R.string.profile_division_add),
                            !state.busy
                        ) { afterSheet { onEditCompetition(-1, group.key) } }
                        TextButton(
                            onClick = { afterSheet { deleteGroup = group.key } },
                            enabled = !state.busy
                        ) {
                            Text(
                                stringResource(R.string.profile_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        TextButton(onClick = { afterSheet {} }) { Text(stringResource(R.string.profile_cancel)) }
                    }
                }
            }
        }
    }

    val pending =
        state.community?.competitions.orEmpty().competitionGroups().find { it.key == deleteGroup }

    if (pending != null) AlertDialog(
        onDismissRequest = { deleteGroup = null },
        title = { Text(stringResource(R.string.profile_competition_delete_title)) },
        text = {
            Text(
                stringResource(
                    R.string.profile_competition_delete_body,
                    competitionTitle(pending),
                    pending.entries.size
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                model.deleteGroup(pending.key); deleteGroup = null
            }, enabled = !state.busy) { Text(stringResource(R.string.profile_delete)) }
        },
        dismissButton = {
            TextButton(onClick = {
                deleteGroup = null
            }) { Text(stringResource(R.string.profile_cancel)) }
        })
    cropUri?.let { uri ->
        ProfileCropDialog(uri, onCancel = { cropUri = null }, onCropped = { output ->
            cropUri = null; preview = output; model.uploadImage(output, false)
        })
    }
    BackHandler(enabled = state.imageOperation == ImageOperation.VERIFICATION) {}
    if (state.imageOperation == ImageOperation.VERIFICATION) Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable {}, contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

private fun competitionTitle(group: CompetitionGroup): String {
    val name = group.first.competitionName.orEmpty()
    val year = group.first.competitionYear?.toString().orEmpty()
    return if (year.isEmpty() || name.contains(year)) name else "$year $name"
}

@Composable
private fun VerificationSheet(state: ProfileUiState, onAction: (Boolean) -> Unit) {
    val record = state.user?.ownerVerification ?: OwnerVerification()
    val status =
        if (record.status == VerificationStatus.NONE && state.user?.role == "OWNER") VerificationStatus.APPROVED else record.status
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(
                when (status) {
                    VerificationStatus.APPROVED -> R.string.profile_verification_approved; VerificationStatus.PENDING -> R.string.profile_verification_pending; else -> R.string.profile_verification
                }
            ), style = MaterialTheme.typography.titleLarge
        )
        if (status == VerificationStatus.APPROVED) Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            androidx.compose.foundation.Image(
                androidx.compose.ui.res.painterResource(R.drawable.profile_icon_badge_check_light),
                null,
                Modifier.size(24.dp)
            )
            Text(state.nickname, style = MaterialTheme.typography.titleLarge)
        }
        else {
            if (status == VerificationStatus.REJECTED) Text(
                record.rejectReason ?: stringResource(R.string.profile_verification_rejected),
                color = MaterialTheme.colorScheme.error
            )
            Text(stringResource(R.string.profile_verification_description))
            if (status == VerificationStatus.PENDING && record.documentUrl != null)
                AsyncImage(
                    record.documentUrl,
                    stringResource(R.string.profile_verification_document),
                    Modifier.size(90.dp)
                )
            ProfileSubmit(
                stringResource(if (status == VerificationStatus.PENDING && record.documentUrl != null) R.string.profile_verification_reupload else R.string.profile_verification_upload),
                !state.busy
            ) { onAction(true) }
        }
        TextButton(onClick = { onAction(false) }) { Text(stringResource(if (status == VerificationStatus.APPROVED) R.string.profile_confirm else R.string.profile_cancel)) }
    }
}
