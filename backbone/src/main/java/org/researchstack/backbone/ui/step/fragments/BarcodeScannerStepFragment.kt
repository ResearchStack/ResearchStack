package org.researchstack.backbone.ui.step.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.researchstack.backbone.R

internal class BarcodeScannerStepFragment : BaseStepFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.rsb_fragment_barcode_scanner_step, container, false)
    }

}