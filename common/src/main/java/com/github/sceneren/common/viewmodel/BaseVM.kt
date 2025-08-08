package com.github.sceneren.common.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.lifecycle.ViewModel
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStart
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.rememberOnRoute
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

abstract class BaseVM<STATE : Any, EFFECT : Any>(state: STATE) : ViewModel(),
    ContainerHost<STATE, EFFECT> {
    override val container: Container<STATE, EFFECT> = container(state) {
        this@BaseVM.initLoad()
    }

    /**
     * 初始化执行
     */
    open fun initLoad() = intent {

    }

    public override fun onCleared() {
        super.onCleared()
    }
}

inline fun <STATE : Any, EFFECT : Any> LifecycleOwner.autoClear(
    crossinline block: () -> BaseVM<STATE, EFFECT>
) {
    lifecycle.doOnDestroy {
        block().onCleared()
    }
}

/**
 * 获取路由生命周期绑定的viewModel
 */
@Composable
inline fun <STATE : Any, EFFECT : Any, reified T : BaseVM<STATE, EFFECT>> rememberVMOnRoute(
    noinline block: @DisallowComposableCalls RouterContext.() -> T
): T = rememberOnRoute {
    block().apply {
        doOnDestroy { onCleared() }
    }
}

/**
 * 创建的时候执行一次
 */
@OptIn(ExperimentalUuidApi::class)
@Composable
inline fun LifecycleEffectCreateOnce(
    key: Any? = null,
    crossinline block: RouterContext.() -> Unit
) {
    rememberOnRoute(key = key ?: Uuid.random()) {
        doOnStart(isOneTime = true) {
            block()
        }
    }
}

