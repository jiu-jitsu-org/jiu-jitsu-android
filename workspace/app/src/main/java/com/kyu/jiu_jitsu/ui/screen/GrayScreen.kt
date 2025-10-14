package com.kyu.jiu_jitsu.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyu.jiu_jitsu.ui.components.textfield.ClearableOutlineTextField
import com.kyu.jiu_jitsu.ui.components.CommonDialog
import com.kyu.jiu_jitsu.ui.components.card.DraggableFlipCard
import com.kyu.jiu_jitsu.ui.components.textfield.TransparentOutlinedTextField
import com.kyu.jiu_jitsu.ui.components.textfield.ValidationState
import com.kyu.jiu_jitsu.ui.theme.CoolGray75
import com.kyu.jiu_jitsu.ui.theme.KakaoBg
import com.kyu.jiu_jitsu.ui.theme.Red500

@Composable
fun GrayScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var isShowDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CoolGray75,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            DraggableFlipCard(
                modifier = Modifier.size(200.dp),
                front = {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Red500
                    ) {
                        Text("Front")
                    }
                },
                back = {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = KakaoBg
                    ) {
                        Text("Back")
                    }
                }
            )

            Button(
                onClick = {
                    isShowDialog = !isShowDialog
                }
            ) {
                Text("Show Dialog")
            }
        }

        if (isShowDialog) {
            CommonDialog(
                title = "Title",
                message = "Message",
                primaryButtonText = "Primary",
                onPrimaryClick = {
                    isShowDialog = !isShowDialog
                },
                onDismissRequest = {
                    isShowDialog = !isShowDialog
                },
                secondaryButtonText = "Secondary",
                onSecondaryClick = {
                    isShowDialog = !isShowDialog
                },
                dismissOnClickOutside = false
            )
        }
    }

}