package kr.bjj_oss.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.model.*
import kr.bjj_oss.ui.theme.Blue300
import kr.bjj_oss.ui.theme.Blue500
import kr.bjj_oss.ui.theme.CoolGray200
import kr.bjj_oss.ui.theme.White
import kr.bjj_oss.ui.theme.WhiteOpacity80

@Composable
fun MyProfileContent(
    state: ProfileUiState,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    preview: String?,
    onNickname: () -> Unit,
    onAcademy: () -> Unit,
    onBelt: () -> Unit,
    onStyle: (String) -> Unit,
    onCompetition: () -> Unit,
    onGroup: (String) -> Unit,
    onImage: () -> Unit,
    onVerification: () -> Unit,
    onSettings: () -> Unit,
    onCommunity: () -> Unit,
    onLogin: () -> Unit,
    onRetry: () -> Unit,
    onMove: (String, Int) -> Unit,
    onCancelOrder: () -> Unit,
    onSaveOrder: () -> Unit,
    onBeginOrder: () -> Unit,
    onChrome: (Boolean) -> Unit,
    onBack: (() -> Unit)? = null,
) {
    val belt = state.community?.beltRank
    val background by animateColorAsState(
        when (belt) {
            is BELT_RANK.WHITE -> CoolGray200; is BELT_RANK.BLUE -> Blue500
            is BELT_RANK.PURPLE -> Color(0xFFCD57FF); is BELT_RANK.BROWN -> Color(0xFFA66040)
            is BELT_RANK.BLACK -> Color(0xFF2D2D2D); else -> Blue300
        }, label = "profileHeader"
    )
    val foreground = if (background.luminance() > 0.45f) Color(0xFF242830) else White
    val list = rememberLazyListState()
    val scrolled by remember { derivedStateOf { list.firstVisibleItemIndex > 0 || list.firstVisibleItemScrollOffset > 0 } }
    LaunchedEffect(scrolled, background) { onChrome(scrolled || background.luminance() > 0.45f) }
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val statusTop = maxOf(statusBarHeight, padding.calculateTopPadding())
    var appBarHeightPx by remember { mutableIntStateOf(0) }
    val appBarHeight =
        maxOf(statusTop + 56.dp, with(LocalDensity.current) { appBarHeightPx.toDp() })
    val statusBarBackground by animateColorAsState(
        targetValue = if (scrolled) WhiteOpacity80 else Color.Transparent,
        label = "profileStatusBarBackground",
    )
    val appBarForeground = if (scrolled) Color(0xFF242830) else foreground
    val bottomPadding = maxOf(
        padding.calculateBottomPadding(),
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    )

    Box(
        modifier
            .fillMaxSize()
            .background(White)
    ) {
        LazyColumn(
            state = list, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding + 24.dp)
        ) {
            item {
                // This space belongs to the first scroll item, so it scrolls away with the
                // header. Later content can pass behind the always-transparent action toolbar.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(background)
                        .padding(top = appBarHeight),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(20.dp))
                    ProfileAvatar(state, preview, onImage)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .widthIn(max = 560.dp)
                            .padding(horizontal = 20.dp)
                            .heightIn(min = 48.dp)
                            .clickable(
                                enabled = !state.isGuest && !state.busy,
                                onClick = onNickname
                            ), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (state.user?.role == "OWNER") Image(
                            painterResource(R.drawable.profile_icon_badge_check_light),
                            stringResource(R.string.profile_verification_approved),
                            Modifier.size(24.dp)
                        )
                        Text(
                            if (state.isGuest) stringResource(R.string.profile_hello) else state.nickname,
                            style = MaterialTheme.typography.titleLarge,
                            color = foreground,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!state.isGuest) Icon(
                            painterResource(R.drawable.profile_icon_pencil),
                            null,
                            Modifier
                                .padding(start = 8.dp)
                                .size(18.dp),
                            tint = foreground
                        )
                    }
                    if (state.isGuest) Text(
                        stringResource(R.string.profile_guest_description),
                        Modifier.padding(horizontal = 20.dp),
                        color = foreground.copy(alpha = 0.7f)
                    )
                    else TextButton(
                        onClick = onAcademy,
                        enabled = !state.busy && state.community != null
                    ) {
                        Text(state.community?.academyName?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.profile_input_academy),
                            color = foreground.copy(alpha = 0.8f))
                        if (!state.community?.academyName.isNullOrBlank()) Icon(
                            painterResource(R.drawable.profile_icon_pencil),
                            null,
                            Modifier
                                .padding(start = 6.dp)
                                .size(16.dp),
                            tint = foreground
                        )
                    }
                    Spacer(Modifier.height(36.dp))
                }
            }
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(background)
                    )
                    Column(
                        Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                            .padding(horizontal = 20.dp)
                    ) {
                        Surface(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 4.dp,
                            color = White
                        ) {
                            Row(
                                Modifier
                                    .clickable(
                                        enabled = !state.busy && (state.isGuest || state.community != null),
                                        onClick = if (state.isGuest) onLogin else onBelt
                                    )
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(painterResource(beltIcon(belt)), null, Modifier.size(40.dp))
                                Column(
                                    Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        stringResource(if (belt == null) R.string.profile_belt_question else R.string.profile_belt_label),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    if (belt != null) Text(
                                        listOf(
                                            beltLabel(belt),
                                            stripeLabel(state.community?.beltStripe)
                                        ).filter { it.isNotBlank() }.joinToString(" "),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    else TextButton(
                                        onClick = if (state.isGuest) onLogin else onBelt,
                                        enabled = !state.busy && (state.isGuest || state.community != null)
                                    ) {
                                        Text(stringResource(if (state.isGuest) R.string.profile_login else R.string.profile_belt_register))
                                    }
                                }
                            }
                        }
                        if (!state.isGuest) {
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                onClick = onCommunity,
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFF5F6F8)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp)
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        stringResource(R.string.profile_community),
                                        Modifier.weight(1f)
                                    )
                                    Icon(
                                        painterResource(kr.bjj_oss.ui.R.drawable.ic_arow_right),
                                        null,
                                        Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                            .padding(horizontal = 20.dp, vertical = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.loading || !state.sessionReady) LinearProgressIndicator(Modifier.fillMaxWidth())
                        if (state.loadFailed) {
                            Text(
                                stringResource(R.string.profile_load_error),
                                color = MaterialTheme.colorScheme.error
                            )
                            TextButton(onClick = onRetry, enabled = !state.refreshing) {
                                Text(
                                    stringResource(R.string.profile_retry)
                                )
                            }
                        }
                        if (state.community != null || state.isGuest) {
                            if (state.showsDefault) EmptyStyles(
                                !state.isGuest && !state.busy,
                                onStyle
                            )
                            else {
                                CompetitionSection(
                                    state,
                                    onCompetition,
                                    onGroup,
                                    onMove,
                                    onCancelOrder,
                                    onSaveOrder,
                                    onBeginOrder
                                )
                                Spacer(Modifier.height(20.dp))
                                FavoriteSection(state, onStyle)
                            }
                        }
                    }
                }
            }
        }
        // Only the system status bar (clock/battery area) changes its background on scroll.
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(statusBarBackground)
        )
        ProfileTopAppBar(
            state = state,
            foreground = appBarForeground,
            topInset = statusTop,
            onBack = onBack,
            onVerification = onVerification,
            onSettings = onSettings,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { appBarHeightPx = it.height },
        )
    }
}

/** Always-transparent action overlay; its measured height is reserved in the first scroll item. */
@Composable
private fun ProfileTopAppBar(
    state: ProfileUiState,
    foreground: Color,
    topInset: androidx.compose.ui.unit.Dp,
    onBack: (() -> Unit)?,
    onVerification: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var more by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(Modifier.padding(top = topInset), contentAlignment = Alignment.TopCenter) {
            Row(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) IconButton(onClick = onBack) {
                    Icon(
                        painterResource(R.drawable.profile_icon_arrow_left),
                        stringResource(R.string.profile_back),
                        tint = foreground
                    )
                }
                Spacer(Modifier.weight(1f))
                if (!state.isGuest && state.sessionReady) Box {
                    IconButton(onClick = { more = !more }, enabled = !state.busy) {
                        Icon(
                            painterResource(R.drawable.profile_icon_menu),
                            stringResource(R.string.profile_more),
                            tint = foreground
                        )
                    }
                    DropdownMenu(expanded = more, onDismissRequest = { more = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.profile_verification)) },
                            onClick = { more = false; onVerification() },
                            enabled = state.user != null,
                        )
                    }
                }
                if (onBack == null) IconButton(onClick = { more = false; onSettings() }) {
                    Icon(
                        painterResource(R.drawable.profile_settings),
                        stringResource(R.string.profile_settings),
                        tint = foreground,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

private fun beltIcon(belt: BELT_RANK?) = when (belt) {
    is BELT_RANK.BLACK -> R.drawable.profile_icon_belt_black; is BELT_RANK.BROWN -> R.drawable.profile_icon_belt_brown
    is BELT_RANK.PURPLE -> R.drawable.profile_icon_belt_purple; is BELT_RANK.BLUE -> R.drawable.profile_icon_belt_blue
    is BELT_RANK.WHITE -> R.drawable.profile_icon_belt_white
    else -> R.drawable.ic_belt_and_weight_default
}

@Composable
private fun ProfileAvatar(state: ProfileUiState, preview: String?, onImage: () -> Unit) {
    Box(
        Modifier
            .size(104.dp)
            .clickable(
                enabled = !state.isGuest && !state.busy && state.user != null,
                onClick = onImage
            )
    ) {
        Box(
            Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            val data = if (state.imageOperation == ImageOperation.DELETE) null else preview
                ?: state.user?.profileImageUrl?.takeIf {
                    it.startsWith("https://") || it.startsWith(
                        "http://"
                    )
                }
            AsyncImage(
                data,
                null,
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.profile_avatar),
                error = painterResource(R.drawable.profile_avatar),
                fallback = painterResource(R.drawable.profile_avatar)
            )
            if (state.imageOperation != null && state.imageOperation != ImageOperation.VERIFICATION) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(Modifier.size(28.dp), color = White) }
            }
        }
        if (!state.isGuest) Surface(
            Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF5F6F8)
        ) {
            Icon(
                painterResource(R.drawable.profile_icon_camera),
                stringResource(R.string.profile_camera),
                Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun EmptyStyles(enabled: Boolean, onStyle: (String) -> Unit) {
    Text(
        stringResource(R.string.profile_style_empty_title),
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        stringResource(R.string.profile_style_empty_body),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    if (enabled) Button(onClick = { onStyle("all") }) { Text(stringResource(R.string.profile_style_register)) }
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(282.dp)
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x660090FF))))
    ) {
        val unit = minOf(maxWidth / 320f, 1.dp)
        Image(
            painterResource(R.drawable.profile_card_style_top_position),
            null,
            Modifier
                .align(Alignment.TopCenter)
                .offset(x = unit * -80, y = unit * 59)
                .width(unit * 142)
                .rotate(-16.18f)
        )
        Image(
            painterResource(R.drawable.profile_card_style_escape_defense),
            null,
            Modifier
                .align(Alignment.TopCenter)
                .offset(x = unit * -40, y = unit * 163)
                .width(unit * 132)
                .rotate(5.83f)
        )
        Image(
            painterResource(R.drawable.profile_card_style_arm_lock),
            null,
            Modifier
                .align(Alignment.TopCenter)
                .offset(x = unit * 36, y = unit * 110)
                .width(unit * 154)
                .rotate(-11.04f)
        )
        Image(
            painterResource(R.drawable.profile_card_style_guard_position),
            null,
            Modifier
                .align(Alignment.TopCenter)
                .offset(x = unit * 72, y = unit * 26)
                .width(unit * 151)
                .rotate(16.33f)
        )
    }
}

@Composable
private fun FavoriteSection(state: ProfileUiState, onStyle: (String) -> Unit) {
    Text(stringResource(R.string.profile_favorites), style = MaterialTheme.typography.titleLarge)
    val profile = state.community
    val items = listOf(
        Triple("position", R.string.profile_position, profile?.favoritePosition?.name),
        Triple("technique", R.string.profile_technique, profile?.favoriteTechnique?.name),
        Triple("submission", R.string.profile_submission, profile?.favoriteSubmission?.name)
    )
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = maxWidth < 340.dp || LocalDensity.current.fontScale > 1.3f

        @Composable
        fun Card(item: Triple<String, Int, String?>, modifier: Modifier) {
            Surface(
                onClick = { onStyle(item.first) }, enabled = !state.busy, modifier = modifier,
                shape = RoundedCornerShape(18.dp), color = Color(0xFFF5F6F8)
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Image(painterResource(styleIcon(item.third)), null, Modifier.size(36.dp))
                    Text(stringResource(item.second), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        stringResource(styleLabelRes(item.third)),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        if (stacked) Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach {
                Card(
                    it,
                    Modifier.fillMaxWidth()
                )
            }
        }
        else Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            items.forEach {
                Card(
                    it, Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun MedalIcon(rank: COMPETITION_RANK?, modifier: Modifier = Modifier) {
    Image(
        painterResource(
            when (rank) {
                is COMPETITION_RANK.GOLD -> R.drawable.profile_medal_gold
                is COMPETITION_RANK.SILVER -> R.drawable.profile_medal_silver
                is COMPETITION_RANK.BRONZE -> R.drawable.profile_medal_bronze
                else -> R.drawable.profile_medal_default
            }
        ), medalLabel(rank), modifier.size(24.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompetitionSection(
    state: ProfileUiState,
    onAdd: () -> Unit,
    onGroup: (String) -> Unit,
    onMove: (String, Int) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onBegin: () -> Unit
) {
    val groups = state.community?.competitions.orEmpty().competitionGroups()
    val ordered = state.orderDraft?.mapNotNull { key -> groups.find { it.key == key } } ?: groups
    val latest by rememberUpdatedState(ordered)
    val move by rememberUpdatedState(onMove)
    val haptic = LocalHapticFeedback.current
    var expanded by rememberSaveable { mutableStateOf(emptyList<String>()) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.profile_competitions),
            Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge
        )
        if (state.orderDraft != null) {
            TextButton(
                onClick = onCancel,
                enabled = !state.busy
            ) { Text(stringResource(R.string.profile_cancel)) }
            TextButton(
                onClick = onSave,
                enabled = !state.busy
            ) { Text(stringResource(R.string.profile_save)) }
        } else IconButton(
            onClick = onAdd,
            enabled = !state.busy
        ) {
            Icon(
                painterResource(R.drawable.profile_icon_plus),
                stringResource(R.string.profile_competition_add)
            )
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val count = state.community?.medalCount ?: MedalCount()
        listOf(
            COMPETITION_RANK.GOLD() to count.gold,
            COMPETITION_RANK.SILVER() to count.silver,
            COMPETITION_RANK.BRONZE() to count.bronze
        ).filter { it.second > 0 }.forEach { (rank, total) ->
            Row(verticalAlignment = Alignment.CenterVertically) { MedalIcon(rank); Text(total.toString()) }
        }
    }
    if (groups.isEmpty()) Surface(
        onClick = onAdd,
        enabled = !state.busy,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF5F6F8)
    ) {
        Text(
            stringResource(R.string.profile_competition_prompt),
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    } else Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF5F6F8)) {
        Column(Modifier.padding(vertical = 8.dp)) {
            ordered.forEach { group ->
                key(group.key) {
                    val isExpanded = group.key in expanded
                    val up = stringResource(R.string.profile_move_up);
                    val down = stringResource(R.string.profile_move_down)
                    val expansion =
                        stringResource(if (isExpanded) R.string.profile_collapse else R.string.profile_expand)
                    var height by remember { mutableIntStateOf(1) }
                    val canMove: (Int) -> Boolean = { direction ->
                        val index = latest.indexOfFirst { it.key == group.key }
                        latest.getOrNull(index + direction)?.pinned == group.pinned
                    }
                    Column(
                        Modifier
                            .onSizeChanged { height = it.height }
                            .semantics {
                                if (group.entries.size > 1) stateDescription = expansion
                                customActions = listOf(CustomAccessibilityAction(up) {
                                    if (canMove(-1)) {
                                        move(group.key, -1); true
                                    } else false
                                }, CustomAccessibilityAction(down) {
                                    if (canMove(1)) {
                                        move(group.key, 1); true
                                    } else false
                                })
                            }
                            .pointerInput(group.key, state.busy) {
                                if (!state.busy) {
                                    var drag = 0f
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { drag = 0f },
                                        onDrag = { change, amount ->
                                            change.consume(); drag += amount.y
                                            val direction = if (drag > 0) 1 else -1
                                            if (kotlin.math.abs(drag) > height / 2f && canMove(
                                                    direction
                                                )
                                            ) {
                                                move(group.key, direction); drag =
                                                    0f; haptic.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                            }
                                        })
                                }
                            }) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !state.busy && state.orderDraft == null) {
                                    if (group.entries.size > 1) expanded =
                                        if (isExpanded) expanded - group.key else expanded + group.key else onGroup(
                                        group.key
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                Modifier
                                    .width(8.dp)
                                    .height(24.dp)
                                    .background(
                                        when (group.belt) {
                                            is BELT_RANK.BLUE -> Blue500; is BELT_RANK.PURPLE -> Color(
                                            0xFFCD57FF
                                        ); is BELT_RANK.BROWN -> Color(0xFFA66040); is BELT_RANK.BLACK -> Color.Black; else -> Color.White
                                        }, RoundedCornerShape(2.dp)
                                    )
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    group.first.competitionName.orEmpty(),
                                    style = if (group.pinned) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    if (group.entries.size > 1) stringResource(
                                        R.string.profile_division_count,
                                        group.entries.size,
                                        group.first.competitionYear ?: 0
                                    )
                                    else listOf(
                                        divisionLabel(group.first, false),
                                        group.first.competitionYear?.toString()
                                    ).filterNotNull().joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!isExpanded) Column {
                                val medals =
                                    group.entries.filter { it.value.competitionRank !is COMPETITION_RANK.PARTICIPATION && it.value.competitionRank != null }
                                if (medals.isEmpty()) MedalIcon(COMPETITION_RANK.PARTICIPATION())
                                else medals.sortedBy { medalOrder(it.value.competitionRank) }
                                    .forEach { MedalIcon(it.value.competitionRank) }
                            }
                            if (group.entries.size > 1) Text(if (isExpanded) "−" else "+")
                        }
                        AnimatedVisibility(isExpanded) {
                            Column {
                                group.entries.sortedBy { medalOrder(it.value.competitionRank) }
                                    .forEach { entry ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = !state.busy && state.orderDraft == null) {
                                                    onGroup(group.key)
                                                }
                                                .padding(horizontal = 28.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                divisionLabel(
                                                    entry.value,
                                                    group.entries.mapNotNull { it.value.belt }
                                                        .distinct().size > 1
                                                ), Modifier.weight(1f)
                                            )
                                            MedalIcon(entry.value.competitionRank)
                                        }
                                    }
                            }
                        }
                        if (state.orderDraft != null) Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onMove(group.key, -1) },
                                enabled = !state.busy && canMove(-1)
                            ) { Text(up) }
                            TextButton(
                                onClick = { onMove(group.key, 1) },
                                enabled = !state.busy && canMove(1)
                            ) { Text(down) }
                        }
                    }
                }
            }
        }
    }
    if (groups.size > 1 && state.orderDraft == null) TextButton(
        onClick = onBegin,
        enabled = !state.busy
    ) { Text(stringResource(R.string.profile_order)) }
}

private fun medalOrder(rank: COMPETITION_RANK?) = when (rank) {
    is COMPETITION_RANK.GOLD -> 0; is COMPETITION_RANK.SILVER -> 1; is COMPETITION_RANK.BRONZE -> 2; else -> 3
}
