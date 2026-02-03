package com.kyu.jiu_jitsu.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kyu.jiu_jitsu.domain.usecase.community.UpdateCommunityProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModifyMyStyleViewModel @Inject constructor(
    private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase,
): ViewModel()  {

    /** Tab Index ( 0 == Best, 1 == Favorite ) **/
    var styleTabIndex by mutableIntStateOf(0)
    var bestTabTitle by mutableStateOf<String?>(null)
    var favoriteTabTitle by mutableStateOf<String?>(null)

}