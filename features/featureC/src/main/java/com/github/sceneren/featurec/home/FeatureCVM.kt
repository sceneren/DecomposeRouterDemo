package com.github.sceneren.featurec.home

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.container

sealed class FeatureC2Effect{
    data class ShowToast(val message: String):FeatureC2Effect()
}

class FeatureCVM : ViewModel(), ContainerHost<FeatureCState, FeatureCEffect> {

    private val _effect = MutableSharedFlow<FeatureC2Effect>()
    val effect: SharedFlow<FeatureC2Effect> = _effect.asSharedFlow()

    override val container = container<FeatureCState, FeatureCEffect>(FeatureCState()) {
//        intent {
//            reduce {
//                state.copy(appKey = uniffi.common.decryptString2("eyJpdiI6ImNqQnpOVkJHU21kelJURnhkVmQ2V0E9PSIsInZhbHVlIjoiam1IMkZDUWczQ3ZNY2NVUzdxZitZRlZZK1dBUVZcL0Q3ckxzRG4wVkhzU0Q0UWFwUFFVejYyWHIrTXl2NUIyVlEzUEdJR21JQWdJZDR2Sk9qaStKdFFGYXdNSnJZVzAxZElZV2hzWUVkbUVlVlVYaTRhSmZ4KzVNakVUcGN6SWdMbUd0TkdwRm1ETGJYNVFEZ0RiVkVRUm14eUZObUo5eHdla2pUOXVHVnpFYk1ZMlJcL2dwVjV0RldhZzFUM0I4d3c2dDlnN3BIcGtCS2VDeGVjRklsYTE5clhxaWNxSHNPMHU0NmdVSlorNG1DcEtIdzFFRFBnbkdBazZZN3FOWHBhUk1uM1FjT3FcL1RcL1l5ckRqVW1rcFhzeHhwWnNuaFBcL2owS1NSK0wwQWdBSkZBZWZ1cmxtRWtQMTBVcVJyTHlXZjI5U1A3Q3BQUGkrTTdMa0FHZEoySlVcL1FqRWFzaEN2cGdndEtFQ2pSQXZ4Q3JMdzJPNnl4dFdxek42K0pXaG0zVGpcL2hOS0N1OW5ENmM4dEQrZHBadVo5RHlRR1JGNnZvMWlFbGJxSld2aWtwQXR2M1l4NVlSUEYxenV6WWE4NnFEYXg4RzF4N3FkeG9LTGQxUW9kWEp6UjNkSXNoMHJvRmw0WTJocjdESTFSUFd0MmZwREFJYnJDWFUzVzh6WDhpVVY3ZjNqdlRHU2ZJUE1kMm51MzBJQVB2Q05xWHpFY1wvNDY3bXFzRmpROGtaZU9CM1JHN1VlcGIyVHFqR0tqU2RoYVwvSTVoblhlRGJxeDVDbUtzQzUyd0tWalVSYWtJRHZseDJ5SFZuWWZHSzdzZXRLRytrRVQwZkpWU3diek80NjVWZ3I2TUNEbmJmSkg0NHZEclc2MlhWXC92WmZ0SzBNY2dvUWdFRzd2Y0d1RldCaEFaV1ZDT29NMndNdmNnRUdaUXgybUVmSnZtT0tFejJmbUJTandYZWFyQjVFNUk2U21MckN6aHM4YWpOMk1PdkluczFxcFJza05EeXYxZTJES2Y3cmRadGlvMit3c1BNcEtNdXBjM2dqZUFyM2tObkpDUllLUFdRcU1haE1OU3JJTUVZblQyMG1KTEpuZjFQQlQxQldCMjF0akJMV2tKTk01TUd4OWx3N3k3bzBJd3pZdjNTMW5PeXpJNzJhcGxZNGQ2VFFSVzd6SGtPcWJBZklod1Vjb0VyZVMzWG1BcFlOK1RFd1MyVjZMd3F4a3RoZDR3RWZ4T3NybjE2a3kxcDZ4QUpVaFZuZDNnOVhDcHlQN09IMTZ2ejBqa2wrUHZvdUY3ekE3ZitaV0ZLdHFlVXhcL21OWkYyOVdFdFI4PSJ9"))
//            }
//        }
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
        val result = com.github.sceneren.uniffi.add(state.number1, state.number2)
        reduce {
            state.copy(result = result)
        }
        _effect.emit(FeatureC2Effect.ShowToast("计算成功"))
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