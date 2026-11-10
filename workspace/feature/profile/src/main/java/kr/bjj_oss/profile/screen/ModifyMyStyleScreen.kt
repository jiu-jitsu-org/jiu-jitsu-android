package kr.bjj_oss.profile.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.components.style.CardBackLayout
import kr.bjj_oss.profile.components.style.CardFrontLayout
import kr.bjj_oss.profile.components.style.CardIndicatorLayout
import kr.bjj_oss.profile.components.style.SegmentedPositionTabBar
import kr.bjj_oss.profile.components.styleLabelRes
import kr.bjj_oss.profile.model.*
import kr.bjj_oss.profile.viewmodel.ModifyMyStyleViewModel
import kr.bjj_oss.profile.viewmodel.StyleEditorState
import kr.bjj_oss.ui.components.button.PrimaryButton
import kr.bjj_oss.ui.components.button.TintButton
import kr.bjj_oss.ui.components.card.DraggableFlipCard
import kr.bjj_oss.ui.theme.ColorComponents
import kr.bjj_oss.ui.theme.CoolGray25
import kr.bjj_oss.ui.theme.WhiteOpacity40
import kr.bjj_oss.ui.theme.getBgDrawableRes
import kr.bjj_oss.ui.theme.getIconDrawableRes

/** Keeps the existing navigation API and repository-backed editor flow behind the restored UI. */
@Composable
fun ModifyMyStyleScreen(
    modifier: Modifier,
    padding: PaddingValues,
    type: String,
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val model = hiltViewModel<ModifyMyStyleViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val complete by rememberUpdatedState(onCompleteClick)
    LaunchedEffect(type) { model.initialize(type) }
    LaunchedEffect(model) {
        model.events.collect { result ->
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            if (result.close) complete()
        }
    }
    MyStyleEditorContent(
        state = state,
        padding = padding,
        showSkip = type == "all",
        onBack = onBackClick,
        onTab = model::selectTab,
        onSelect = model::select,
        onComplete = model::completeSelection,
        onRetry = { model.retry(type) },
        modifier = modifier,
    )
}

private data class StyleCardVisual(
    val wire: String,
    val background: Int,
    val icon: Int,
)

@Composable
private fun MyStyleEditorContent(
    state: StyleEditorState,
    padding: PaddingValues,
    showSkip: Boolean,
    onBack: () -> Unit,
    onTab: (Int) -> Unit,
    onSelect: (String?) -> Unit,
    onComplete: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = remember(state.page) {
        when (state.page) {
            0 -> POSITION_LIST.map {
                StyleCardVisual(
                    it.name,
                    it.getBgDrawableRes(),
                    it.getIconDrawableRes()
                )
            }

            1 -> TECHNIQUE_LIST.map {
                StyleCardVisual(
                    it.name,
                    it.getBgDrawableRes(),
                    it.getIconDrawableRes()
                )
            }

            else -> SUBMISSION_LIST.map {
                StyleCardVisual(
                    it.name,
                    it.getBgDrawableRes(),
                    it.getIconDrawableRes()
                )
            }
        }
    }
    val indicators = when (state.page) {
        0 -> POSITION_INDICATOR_LIST
        1 -> TECHNIQUE_INDICATOR_LIST
        else -> SUBMISSION_INDICATOR_LIST
    }
    val selectedIndex = options.indexOfFirst { it.wire == state.visibleSelection }
    // Keep the original default artwork without assigning that preview to the stored selection.
    val preview = options.getOrElse(selectedIndex) { options.first() }
    val title = stringResource(styleCardTitle(preview.wire))
    val category = stringResource(
        when (state.page) {
            0 -> R.string.profile_position
            1 -> R.string.profile_technique
            else -> R.string.profile_submission
        }
    )
    val titleRes = when (state.page) {
        0 -> R.string.profile_my_style_position_title
        1 -> R.string.profile_my_style_technique_title
        else -> R.string.profile_my_style_submission_title
    }
    val bestTitle = state.bestSelected?.let { stringResource(styleCardTitle(it)) }
        ?: stringResource(styleCardTitle(options.first().wire))
    val favoriteTitle = state.selected?.let { stringResource(styleCardTitle(it)) }
        ?: stringResource(R.string.profile_style_input_prompt)
    val shape = RoundedCornerShape(24.dp)
    val fontScale = LocalDensity.current.fontScale
    val enabled = state.ready && !state.saving
    var footerHeightPx by remember { mutableIntStateOf(0) }
    val footerHeight =
        with(LocalDensity.current) { footerHeightPx.toDp() }.takeIf { it > 0.dp } ?: 132.dp

    Surface(modifier.fillMaxSize(), color = CoolGray25) {
        BoxWithConstraints(Modifier
            .padding(padding)
            .fillMaxSize()) {
            val cardAspectRatio = 262f / 394f
            val cardMaxHeight = (maxHeight * 0.49f).coerceAtMost(420.dp)
            // The phone composition matches the original. Short windows scroll a readable card.
            val readableCardHeight = maxOf(cardMaxHeight, 320.dp)
            val cardWidth = minOf(maxWidth * 0.76f, readableCardHeight * cardAspectRatio)
            val cardHeight =
                maxOf(cardWidth / cardAspectRatio, 320.dp * (fontScale - 0.3f).coerceAtLeast(1f))
            val topContentPadding = (maxHeight * 0.09f).coerceIn(52.dp, 92.dp)
            val tabCardSpacing = (maxHeight * 0.035f).coerceIn(20.dp, 32.dp)

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp, bottom = footerHeight),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = (topContentPadding - 56.dp).coerceAtLeast(0.dp),
                            bottom = 20.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SegmentedPositionTabBar(
                        tabs = listOf(
                            StyleTabItem.Best(selectedItem = bestTitle),
                            StyleTabItem.Favorite(selectedItem = favoriteTitle)
                        ),
                        selectedIndex = state.selectedTab,
                        onTabSelected = onTab,
                        modifier = Modifier
                            .fillMaxWidth(0.78f)
                            .widthIn(max = 360.dp),
                        enabled = enabled,
                    )
                    Spacer(Modifier.height(tabCardSpacing))
                    key(state.page, state.selectedTab) {
                        DraggableFlipCard(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight)
                                .shadow(18.dp, shape, clip = false)
                                .clip(shape),
                            front = {
                                CardFrontLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    cardShape = shape,
                                    backgroundRes = preview.background,
                                    iconRes = preview.icon,
                                    title = title,
                                    info = stringResource(styleCardInfo(preview.wire)),
                                )
                            },
                            back = {
                                CardBackLayout(
                                    Modifier.fillMaxSize(),
                                    cardShape = shape,
                                    type = category,
                                    title = title
                                )
                            },
                        )
                    }
                    if (state.error) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.profile_save_error),
                            Modifier.padding(horizontal = 20.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                        if (!state.ready) TextButton(onClick = onRetry) { Text(stringResource(R.string.profile_retry)) }
                    }
                }
            }

            // Original translucent header and centered category title.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(WhiteOpacity40)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, enabled = !state.saving) {
                        Image(
                            painterResource(kr.bjj_oss.ui.R.drawable.ic_gray_left_back_button),
                            stringResource(R.string.profile_back), Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (showSkip) TintButton(
                        text = stringResource(R.string.profile_skip),
                        enabled = !state.saving,
                        onClick = onBack
                    )
                }
                Text(
                    stringResource(titleRes),
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = if (showSkip) 96.dp else 48.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorComponents.Header.Header.Text,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!state.ready || state.saving) LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )

            // Keep the overlapping mini-card/gradient footer on normal font sizes.
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .onSizeChanged { footerHeightPx = it.height }) {
                if (fontScale <= 1.3f) {
                    Box(Modifier
                        .fillMaxWidth()
                        .height(132.dp)) {
                        CardIndicatorLayout(
                            Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(94.dp),
                            items = indicators, selectedIndex = selectedIndex, enabled = enabled,
                            onTabSelected = { index -> onSelect(options[index].wire) },
                        )
                        StyleCompleteButton(
                            Modifier.align(Alignment.BottomCenter),
                            enabled,
                            onComplete
                        )
                    }
                } else {
                    Column {
                        CardIndicatorLayout(
                            Modifier
                                .fillMaxWidth()
                                .height(94.dp * fontScale),
                            items = indicators, selectedIndex = selectedIndex, enabled = enabled,
                            onTabSelected = { index -> onSelect(options[index].wire) })
                        StyleCompleteButton(Modifier, enabled, onComplete)
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleCompleteButton(modifier: Modifier, enabled: Boolean, onComplete: () -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(CoolGray25.copy(alpha = 0f), CoolGray25)))
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp)
    ) {
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            text = stringResource(R.string.profile_my_style_complete),
            roundedCorner = 16.dp,
            enabled = enabled,
            onClick = onComplete,
        )
    }
}

private fun styleCardTitle(wire: String): Int =
    if (wire == "ESCAPES") R.string.profile_style_escape_title else styleLabelRes(wire)

private fun styleCardInfo(wire: String): Int = when (wire) {
    "CHOKES" -> R.string.profile_style_choke_info
    "ARM_LOCKS" -> R.string.profile_style_arm_info
    "LEG_LOCKS" -> R.string.profile_style_leg_info
    "SWEEPS" -> R.string.profile_style_sweep_info
    "GUARD_PASSES" -> R.string.profile_style_pass_info
    "ESCAPES" -> R.string.profile_style_escape_info
    else -> R.string.profile_style_position_info
}
