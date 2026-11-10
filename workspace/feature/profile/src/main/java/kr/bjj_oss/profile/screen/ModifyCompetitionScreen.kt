package kr.bjj_oss.profile.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.bjj_oss.model.*
import kr.bjj_oss.profile.R
import kr.bjj_oss.profile.components.*
import kr.bjj_oss.profile.viewmodel.*
import java.time.Year

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModifyCompetitionScreen(
    modifier: Modifier,
    padding: PaddingValues,
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit,
    originalIndex: Int = -1,
    groupKey: String? = null
) {
    val model = hiltViewModel<ModifyCompetitionViewModel>()
    LaunchedEffect(originalIndex, groupKey) { model.initialize(originalIndex, groupKey) }
    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val complete by rememberUpdatedState(onCompleteClick)
    LaunchedEffect(model) {
        model.events.collect {
            Toast.makeText(
                context,
                it.message,
                Toast.LENGTH_SHORT
            ).show(); if (it.close) complete()
        }
    }
    var deleting by rememberSaveable { mutableStateOf(false) }
    var years by remember { mutableStateOf(false) }
    BackHandler { model.back(onBackClick) }
    ProfileForm(
        stringResource(if (state.editing) R.string.profile_competition_edit else if (state.fixedGroup) R.string.profile_division_add else R.string.profile_competition_add),
        padding,
        { model.back(onBackClick) },
        modifier,
        actions = {
            if (state.editing) TextButton(
                onClick = { deleting = true },
                enabled = !state.saving
            ) { Text(stringResource(R.string.profile_delete)) }
        }) {
        when (state.step) {
            CompetitionStep.RESULT -> {
                Text(
                    stringResource(R.string.profile_competition_result_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                listOf(
                    COMPETITION_RANK.GOLD(),
                    COMPETITION_RANK.SILVER(),
                    COMPETITION_RANK.BRONZE(),
                    COMPETITION_RANK.PARTICIPATION()
                ).forEach { rank ->
                    FilterChip(
                        selected = state.rank == rank.name,
                        onClick = { model.rank(rank.name) },
                        enabled = !state.saving,
                        label = { Text(medalLabel(rank)) },
                        leadingIcon = { MedalIcon(rank) })
                }
            }

            CompetitionStep.NAME -> {
                Text(
                    stringResource(R.string.profile_competition_name_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                OutlinedTextField(
                    state.name,
                    model::name,
                    Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.saving,
                    label = { Text(stringResource(R.string.profile_competition_name)) })
                if (state.searchFailed) Text(stringResource(R.string.profile_search_error))
                state.suggestions.forEach { item ->
                    TextButton(onClick = { model.suggestion(item) }) {
                        Text(
                            listOfNotNull(item.name, item.year?.toString()).joinToString(" · ")
                        )
                    }
                }
            }

            CompetitionStep.YEAR -> {
                Text(
                    stringResource(R.string.profile_competition_date_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Box {
                    OutlinedButton(
                        onClick = { years = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(state.year.toString()) }
                    DropdownMenu(
                        expanded = years,
                        onDismissRequest = { years = false },
                        modifier = Modifier.heightIn(max = 320.dp)
                    ) {
                        (maxOf(Year.now().value, state.year) downTo minOf(
                            1990,
                            state.year
                        )).forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = { model.year(year); years = false })
                        }
                    }
                }
            }

            CompetitionStep.DIVISION -> {
                Text(
                    stringResource(R.string.profile_division),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("${state.year} ${state.name}", style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.profile_uniform))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf<String?>(null, "GI", "NO_GI").forEach { uniform ->
                        FilterChip(
                            selected = state.uniform == uniform,
                            onClick = { model.uniform(uniform) },
                            enabled = !state.saving,
                            label = {
                                Text(
                                    stringResource(
                                        when (uniform) {
                                            "GI" -> R.string.profile_gi; "NO_GI" -> R.string.profile_no_gi; else -> R.string.profile_none
                                        }
                                    )
                                )
                            })
                    }
                }
                Text(stringResource(R.string.profile_competition_belt))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (listOf<BELT_RANK?>(null) + BELT_RANK_LIST).forEach { belt ->
                        FilterChip(
                            selected = state.belt == belt?.name,
                            onClick = { model.belt(belt?.name) },
                            enabled = !state.saving,
                            label = { Text(beltLabel(belt)) })
                    }
                }
                OutlinedTextField(
                    state.weight,
                    model::weight,
                    Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.profile_weight)) },
                    singleLine = true,
                    enabled = !state.saving && !state.unlimited && !state.absolute,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.saving) { model.unlimited(!state.unlimited) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(state.unlimited, model::unlimited, enabled = !state.saving); Text(
                    stringResource(R.string.profile_unlimited)
                )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.saving) { model.absolute(!state.absolute) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(state.absolute, model::absolute, enabled = !state.saving); Text(
                    stringResource(R.string.profile_absolute)
                )
                }
            }

            CompetitionStep.COMPLETE -> {
                Text(
                    stringResource(R.string.profile_competition_complete),
                    style = MaterialTheme.typography.headlineMedium
                )
                val entries = state.completedEntries.ifEmpty { emptyList() }
                entries.forEach { entry ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MedalIcon(
                            entry.competitionRank
                        ); Text(divisionLabel(entry), Modifier.padding(start = 8.dp))
                    }
                }
                ProfileSubmit(stringResource(R.string.profile_added_division)) { model.anotherDivision() }
                ProfileSubmit(
                    stringResource(R.string.profile_to_profile),
                    onClick = onCompleteClick
                )
            }
        }
        state.error?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
        if (state.step != CompetitionStep.COMPLETE) {
            ProfileSubmit(
                stringResource(if (state.step == CompetitionStep.DIVISION) R.string.profile_save else R.string.profile_next),
                !state.saving && (state.step != CompetitionStep.NAME || state.name.isNotBlank()) && state.error != R.string.profile_division_removed
            ) { model.next() }
            if (state.step == CompetitionStep.DIVISION && !state.editing) TextButton(onClick = {
                model.save(
                    true
                )
            }, enabled = !state.saving) { Text(stringResource(R.string.profile_division_skip)) }
        }
        if (state.saving) CircularProgressIndicator()
    }
    if (deleting) AlertDialog(
        onDismissRequest = { deleting = false },
        title = { Text(stringResource(R.string.profile_competition_delete_title)) },
        confirmButton = {
            TextButton(
                onClick = { deleting = false; model.delete() },
                enabled = !state.saving
            ) { Text(stringResource(R.string.profile_delete)) }
        },
        dismissButton = {
            TextButton(onClick = {
                deleting = false
            }) { Text(stringResource(R.string.profile_cancel)) }
        })
}
