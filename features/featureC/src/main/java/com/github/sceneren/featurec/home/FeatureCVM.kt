package com.github.sceneren.featurec.home

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class FeatureCVM : ViewModel(), ContainerHost<FeatureCState, FeatureCEffect> {
    override val container = container<FeatureCState, FeatureCEffect>(FeatureCState()) {
        intent {
            reduce {
                state.copy(appKey = uniffi.common.getAppKey())
            }
        }
    }

    fun setNumber1(number: Int) = intent {
        reduce {
            state.copy(number1 = number)
        }
    }

    fun setNumber2(number: Int) = intent {
        reduce {
            state.copy(number2 = number)
        }
    }

    fun calculate() = intent {
        val result = uniffi.common.add(state.number1, state.number2)
        reduce {
            state.copy(result = result)
        }
    }

    fun changeSearchText(text: String) = blockingIntent {

        reduce {
            state.copy(searchText = text)
        }
    }

    fun search() = intent {
        val list = mutableListOf<String>()
        for (i in 0..10) {
            list.add("${state.searchText}结果-$i")
        }
        reduce {
            state.copy(
                searchExpand = true,
                searchResultList = list.toPersistentList()
            )
        }
    }

    fun changeSearchExpand(expand: Boolean) = intent {
        reduce {
            state.copy(searchExpand = expand)
        }
    }

}