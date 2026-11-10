package kr.bjj_oss.profile.model

import kr.bjj_oss.model.*

/** Entries retain original indices, so equal-valued divisions can be edited independently. */
data class CompetitionGroup(val key: String, val entries: List<IndexedValue<CompetitionInfo>>) {
    val first get() = entries.first().value
    val pinned get() = entries.any { it.value.isPinned }
    val order get() = entries.mapNotNull { it.value.displayOrder }.minOrNull()
    val belt get() = entries.mapNotNull { it.value.belt }.maxByOrNull { BELT_RANK_LIST.indexOf(it) }
}

fun CompetitionInfo.groupKey(): String {
    val year = competitionYear?.toString().orEmpty()
    val name = competitionName.orEmpty().trim()
    val withoutYear = if (year.isEmpty()) name else name.replace(Regex("(?<![0-9])${Regex.escape(year)}(?![0-9])"), "")
    return "$year|${withoutYear.replace(Regex("\\s+"), "").lowercase()}"
}

fun List<CompetitionInfo>.competitionGroups(): List<CompetitionGroup> {
    val groups = withIndex().groupBy { it.value.groupKey() }.map { (key, values) -> CompetitionGroup(key, values) }
    val defaults = compareByDescending<CompetitionGroup> { it.belt?.let(BELT_RANK_LIST::indexOf) ?: -1 }
        .thenByDescending { it.first.competitionYear ?: 0 }.thenBy { it.entries.first().index }
    val pinned = groups.filter { it.pinned }.sortedWith(compareBy<CompetitionGroup> { it.order ?: Int.MAX_VALUE }.then(defaults))
    val ordinary = groups.filterNot { it.pinned }
    val ordered = ordinary.filter { it.order != null }.sortedBy { it.order }.toMutableList()
    ordinary.filter { it.order == null }.sortedWith(defaults).forEach { group ->
        val index = ordered.indexOfFirst { defaults.compare(group, it) < 0 }
        ordered.add(if (index < 0) ordered.size else index, group)
    }
    return pinned + ordered
}

fun List<CompetitionInfo>.inGroupOrder(keys: List<String>, changedPinned: Set<Boolean>): List<CompetitionInfo> {
    val groups = competitionGroups().associateBy { it.key }
    val offsets = mutableMapOf(false to 0, true to 0)
    return keys.flatMap { key ->
        val group = groups[key] ?: return@flatMap emptyList()
        val order = offsets.getValue(group.pinned)
        offsets[group.pinned] = order + 1
        group.entries.map { it.value.copy(displayOrder = if (group.pinned in changedPinned) order else it.value.displayOrder) }
    }
}
