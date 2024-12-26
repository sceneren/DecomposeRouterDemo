package com.github.sceneren.featurea.home

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class FeatureAVM : ViewModel(), ContainerHost<FeatureAState, Nothing> {
    override val container = container<FeatureAState, Nothing>(FeatureAState())
}