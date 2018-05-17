package Util.MVP.Android

import android.content.Context
import com.tinmegali.mvp.mvp.*

abstract class BasePresenter<PresenterModel : Interfaces.PresenterModel,
        Model : Interfaces.Model<PresenterModel>, View : Interfaces.View, ModelImpl : Model>
    : GenericPresenter<PresenterModel, Model, View, ModelImpl>(), Interfaces.PresenterView<View>,
        Interfaces.PresenterModel {

    override val ctx by lazy { view.ctx }

    override fun onConfigurationChanged(view : View) {
        setView(view)
    }

    override fun onBackPressed() {}
}