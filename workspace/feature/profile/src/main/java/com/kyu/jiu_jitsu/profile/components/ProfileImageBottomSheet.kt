package com.kyu.jiu_jitsu.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.profile.R
import com.kyu.jiu_jitsu.ui.R as UiR
import com.kyu.jiu_jitsu.ui.components.button.PrimaryButton
import com.kyu.jiu_jitsu.ui.theme.ColorComponents
import com.kyu.jiu_jitsu.ui.theme.CoolGray25
import com.kyu.jiu_jitsu.ui.theme.CoolGray500
import com.kyu.jiu_jitsu.ui.theme.CoolGray900
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun ProfileImageBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = White)
            .padding(horizontal = 23.dp)
            .padding(bottom = 23.dp),
    ) {
        Spacer(modifier = Modifier.height(17.dp))
        Text(
            text = stringResource(R.string.profile_image_edit_title),
            style = MaterialTheme.typography.titleSmall,
            color = CoolGray900,
        )
        Spacer(modifier = Modifier.height(29.dp))
        ProfileImageActionRow(
            text = stringResource(R.string.profile_image_take_photo),
            isHighlighted = true,
            onClick = onCameraClick,
        )
        Spacer(modifier = Modifier.height(7.dp))
        ProfileImageActionRow(
            text = stringResource(R.string.profile_image_find_album),
            onClick = onGalleryClick,
        )
        Spacer(modifier = Modifier.height(7.dp))
        ProfileImageDeleteRow(
            text = stringResource(R.string.profile_image_delete),
            onClick = onDeleteClick,
        )
        Spacer(modifier = Modifier.height(31.dp))
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            text = stringResource(R.string.profile_image_cancel),
            onClick = onCancelClick,
            roundedCorner = 13.dp,
        )
    }
}

@Composable
private fun ProfileImageActionRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(color = if (isHighlighted) CoolGray25 else White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = CoolGray500,
        )
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(UiR.drawable.ic_arow_right),
            contentDescription = null,
            tint = ColorComponents.BottomSheet.UnSelected.ListItem.FollowingIcon,
        )
    }
}

@Composable
private fun ProfileImageDeleteRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(13.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = CoolGray500,
        )
    }
}
