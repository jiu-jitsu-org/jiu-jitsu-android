package com.kyu.jiu_jitsu.profile.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kyu.jiu_jitsu.model.isShowModify
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightBottomSheet
import com.kyu.jiu_jitsu.profile.components.BeltRankAndWeightLayout
import com.kyu.jiu_jitsu.profile.components.CompetitionLayout
import com.kyu.jiu_jitsu.profile.components.MySkillLayout
import com.kyu.jiu_jitsu.profile.components.ProfileImageBottomSheet
import com.kyu.jiu_jitsu.profile.viewmodel.ProfileViewModel
import com.kyu.jiu_jitsu.profile.viewmodel.ProfileAction
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.components.button.PressableTextButton
import com.kyu.jiu_jitsu.ui.components.button.TintButton
import com.kyu.jiu_jitsu.ui.insets.StatusBarBackground
import com.kyu.jiu_jitsu.ui.routes.SkillStyleScreenType
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray100
import com.kyu.jiu_jitsu.ui.theme.CoolGray500
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.White
import com.kyu.jiu_jitsu.ui.theme.color
import java.io.File
import kotlin.math.roundToInt

/**
 * 프로필 화면
 * @param modifier Modifier
 * @param padding PaddingValues
 * @param onModifyClick 정보 수정 클릭
 * @param savedStateHandle 뒤로가기 정보 연동
 * @param onAcademyClick 도장 정보 클릭
 * @param onMyStyleClick 스타일(포지션, 스킬, 서브미션) 클릭
 * @param onCompetitionClick 대회 정보 추가 클릭
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    onModifyClick: () -> Unit = {},
    savedStateHandle: SavedStateHandle,
    onAcademyClick: (name: String) -> Unit = {},
    onMyStyleClick: (screenName: String) -> Unit = {},
    onCompetitionClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ProfileViewModel>()
    /** Collect State **/
    val loadingState by viewModel.loadingUiState.collectAsStateWithLifecycle()
    val profileInfoState by viewModel.profileInfoUiState.collectAsStateWithLifecycle()
    val errorState by viewModel.errorUiState.collectAsStateWithLifecycle()

    /** 프로필 UI BG Color */
    val profileBgColor = profileInfoState?.beltRank?.color() ?: ColorComponents.MyProfileHeader.Bg.Default

    val beltSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val profileImageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var openBeltBottomSheet by rememberSaveable { mutableStateOf(false) }
    var openProfileImageBottomSheet by rememberSaveable { mutableStateOf(false) }
    var pendingCameraImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cameraPermissionDeniedMessage = stringResource(
        com.kyu.jiu_jitsu.profile.R.string.profile_camera_permission_denied
    )
    val galleryPermissionDeniedMessage = stringResource(
        com.kyu.jiu_jitsu.profile.R.string.profile_gallery_permission_denied
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri?.let {
            selectedProfileBitmap = context.loadProfileBitmap(it) ?: selectedProfileBitmap
            viewModel.uploadCommunityImage(it)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            pendingCameraImageUri
                ?.let(Uri::parse)
                ?.let {
                    selectedProfileBitmap = context.loadProfileBitmap(it)
                    viewModel.uploadCommunityImage(it)
                }
        }
    }
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            Toast.makeText(context, galleryPermissionDeniedMessage, Toast.LENGTH_SHORT).show()
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            val cameraImageUri = context.createProfileImageUri()
            pendingCameraImageUri = cameraImageUri.toString()
            cameraLauncher.launch(cameraImageUri)
        } else {
            Toast.makeText(context, cameraPermissionDeniedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun openGallery() {
        val galleryPermission = getGalleryPermission()
        if (galleryPermission == null || context.hasPermission(galleryPermission)) {
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            galleryPermissionLauncher.launch(galleryPermission)
        }
    }

    fun openCamera() {
        if (context.hasPermission(Manifest.permission.CAMERA)) {
            val cameraImageUri = context.createProfileImageUri()
            pendingCameraImageUri = cameraImageUri.toString()
            cameraLauncher.launch(cameraImageUri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val listState = rememberLazyListState()
    val isFirstItemFullyVisible by remember {
//        derivedStateOf {
//            listState.layoutInfo.visibleItemsInfo.any { it.index == 0 }
//        }
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    /** Navigation Backstack 대응
     *  ex) 프로필 정보 등록 이후 Navigation popup > 정보 갱신 필요
     **/
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            .getStateFlow<String?>("isCompetitionUpdated", null)
            .collect { isCompetitionUpdated ->
                isCompetitionUpdated?.let {
                    if (it.isNotEmpty()) {
                        viewModel.onAction(ProfileAction.FetchProfileData)
                    }
                }
            }
    }

    Surface(
        modifier = modifier,
        color = profileBgColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = White),
                state = listState,
            ) {
                /** 상단 TopBar */
                item {
                    Row(
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth()
                            .background(color = profileBgColor)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        if (profileInfoState?.isShowModify() ?: false) {
                            PressableTextButton(
                                text = stringResource(R.string.common_modify),
                                onClick = onModifyClick,
                                enableTextColor = White,
                                pressedTextColor = White,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(27.dp).fillMaxWidth().background(color = profileBgColor))
                }
                /** 유저 프로필 + 닉네임 */
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(color = profileBgColor),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        /** 프로필 이미지 */
                        EditableProfileImage(
                            selectedProfileBitmap = selectedProfileBitmap,
                            profileImageUrl = profileInfoState?.profileImageUrl,
                            onClick = { openProfileImageBottomSheet = true },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        /** 닉네임 */
                        Text(
                            text = profileInfoState?.nickname ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = ColorComponents.List.Setting.Background
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        /** 도장 정보 입력 / 도장 이름  */
                        if ((profileInfoState?.academyName?:"").isEmpty()) {
                            TintButton(
                                text = stringResource(com.kyu.jiu_jitsu.profile.R.string.profile_input_academy),
                                onClick = { onAcademyClick(profileInfoState?.academyName ?: "") },
                            )
                        } else {
//                        Text(
//                            text = profileAcademyName ?: "",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = CoolGray75,
//                        )
                            TintButton(
                                text = profileInfoState?.academyName ?: "",
                                textStyle = MaterialTheme.typography.titleMedium,
                                enableTextColor = CoolGray75,
                                pressedTextColor = CoolGray75,
                                onClick = { onAcademyClick(profileInfoState?.academyName ?: "") },
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp).fillMaxWidth().background(color = profileBgColor))
                    }
                }
                /** 벨트, 체급 */
                item {
                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(
                                color = White,
                            )
                    ) {
                        val (infoRow, bg) = createRefs()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    color = profileBgColor,
                                )
                                .constrainAs(bg) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                        )

                        Surface(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .constrainAs(infoRow) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                },
                            shadowElevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = ColorComponents.List.Setting.Background,
                        ) {
                            BeltRankAndWeightLayout(
                                beltRank = profileInfoState?.beltRank,
                                beltStripe = profileInfoState?.beltStripe,
                                weightKg = profileInfoState?.weightKg,
                                isWeightHidden = profileInfoState?.isWeightHidden ?: false,
                                onSaveBeltWeightClick = { openBeltBottomSheet = true },
                                onSaveWeightHiddenClick = { isWeightHidden ->
                                    viewModel.changeBeltAndWeight(
                                        isWeightHidden = isWeightHidden
                                    )
                                }
                            )
                        }

                    }

                }
                /** 나의 주짓수 (특기, 포지션, 기술) */
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    MySkillLayout(
                        info = profileInfoState,
                        onSaveMyStyleClick = onMyStyleClick
                    )
                }
                /** 대회 정보 */
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    CompetitionLayout(
                        onAddCompetitionClickEvent = onCompetitionClick
                    )
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }

            /** 상단 흰 상태바 (스크롤 최상단이 아닐 경우에만 노출) */
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = !isFirstItemFullyVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                StatusBarBackground(color = White)
            }
        }

        /** 벨트/체급 선택 바텀 시트 */
        if (openBeltBottomSheet) {
            ModalBottomSheet(
                containerColor = ColorComponents.BottomSheet.Selected.Container.Background,
                contentColor = ColorComponents.BottomSheet.Selected.Container.Title,
                scrimColor = ColorComponents.BottomSheet.Selected.Container.Scrim,
                sheetState = beltSheetState,
                onDismissRequest = {
                    openBeltBottomSheet = false
                },
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        width = 48.dp,
                        height = 4.dp,
                        color = ColorComponents.BottomSheet.Selected.Container.Handle
                    )
                },
            ) {
                BeltRankAndWeightBottomSheet { beltRank, beltStripe, gender, weightKg, isWeightHidden ->
                    openBeltBottomSheet = false
                    viewModel.changeBeltAndWeight(beltRank, beltStripe, gender, weightKg, isWeightHidden)
                }
            }
        }

        /** 프로필 이미지 선택 바텀 시트 */
        if (openProfileImageBottomSheet) {
            ModalBottomSheet(
                containerColor = ColorComponents.BottomSheet.Selected.Container.Background,
                contentColor = ColorComponents.BottomSheet.Selected.Container.Title,
                scrimColor = ColorComponents.BottomSheet.Selected.Container.Scrim,
                sheetState = profileImageSheetState,
                onDismissRequest = {
                    openProfileImageBottomSheet = false
                },
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        width = 48.dp,
                        height = 4.dp,
                        color = ColorComponents.BottomSheet.Selected.Container.Handle
                    )
                },
            ) {
                ProfileImageBottomSheet(
                    onCameraClick = {
                        openProfileImageBottomSheet = false
                        openCamera()
                    },
                    onGalleryClick = {
                        openProfileImageBottomSheet = false
                        openGallery()
                    },
                    onDeleteClick = {
                        selectedProfileBitmap = null
                        openProfileImageBottomSheet = false
                    },
                    onCancelClick = {
                        openProfileImageBottomSheet = false
                    },
                )
            }
        }

    }
}

@Composable
private fun EditableProfileImage(
    selectedProfileBitmap: Bitmap?,
    profileImageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val validProfileImageUrl = remember(profileImageUrl) {
        profileImageUrl?.trim()?.takeIf(String::isNotEmpty)
    }

    Box(
        modifier = modifier
            .size(108.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(color = White),
            contentAlignment = Alignment.Center,
        ) {
            when {
                selectedProfileBitmap != null -> {
                    Image(
                        bitmap = selectedProfileBitmap.asImageBitmap(),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                validProfileImageUrl != null -> {
                    // 프로필 조회 API에서 내려온 profileImage.imageUrl이 존재하면 Coil로 원격 이미지를 로드한다.
                    // 로딩 중이거나 URL 호출이 실패한 경우에도 빈 영역이 보이지 않도록 기본 프로필 이미지를 노출한다.
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(validProfileImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_profile_default),
                        error = painterResource(R.drawable.ic_profile_default),
                        fallback = painterResource(R.drawable.ic_profile_default),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    // 프로필 이미지 URL이 없거나 빈 문자열이면 네트워크 요청 없이 기본 이미지를 바로 표시한다.
                    Icon(
                        modifier = Modifier.size(68.dp),
                        painter = painterResource(R.drawable.ic_profile_default),
                        contentDescription = "Profile Image",
                        tint = CoolGray100,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(14.dp))
                .background(color = CoolGray25),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(com.kyu.jiu_jitsu.profile.R.drawable.ic_profile_camera),
                contentDescription = null,
                tint = CoolGray500,
            )
        }
    }
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun getGalleryPermission(): String? =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        null
    }

private fun Context.loadProfileBitmap(uri: Uri): Bitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val imageWidth = info.size.width
            val imageHeight = info.size.height
            val scale = minOf(
                PROFILE_IMAGE_DECODE_SIZE / imageWidth.toFloat(),
                PROFILE_IMAGE_DECODE_SIZE / imageHeight.toFloat(),
                1f,
            )
            if (scale < 1f) {
                decoder.setTargetSize(
                    (imageWidth * scale).roundToInt(),
                    (imageHeight * scale).roundToInt(),
                )
            }
        }
    } else {
        decodeBitmapFromStream(uri)
    }
}.getOrNull()

private fun Context.createProfileImageUri(): Uri {
    val imageDir = File(cacheDir, PROFILE_IMAGE_CACHE_DIR).apply {
        mkdirs()
    }
    val imageFile = File.createTempFile(
        PROFILE_IMAGE_FILE_PREFIX,
        PROFILE_IMAGE_FILE_SUFFIX,
        imageDir,
    )
    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        imageFile,
    )
}

private fun Context.decodeBitmapFromStream(uri: Uri): Bitmap? {
    val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    contentResolver.openInputStream(uri)?.use { input ->
        BitmapFactory.decodeStream(input, null, boundsOptions)
    }

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(
            width = boundsOptions.outWidth,
            height = boundsOptions.outHeight,
            targetSize = PROFILE_IMAGE_DECODE_SIZE,
        )
    }
    return contentResolver.openInputStream(uri)?.use { input ->
        BitmapFactory.decodeStream(input, null, decodeOptions)
    }
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    targetSize: Int,
): Int {
    var sampleSize = 1
    while (width / sampleSize > targetSize || height / sampleSize > targetSize) {
        sampleSize *= 2
    }
    return sampleSize
}

private const val PROFILE_IMAGE_DECODE_SIZE = 512
private const val PROFILE_IMAGE_CACHE_DIR = "profile_images"
private const val PROFILE_IMAGE_FILE_PREFIX = "profile_image_"
private const val PROFILE_IMAGE_FILE_SUFFIX = ".jpg"
