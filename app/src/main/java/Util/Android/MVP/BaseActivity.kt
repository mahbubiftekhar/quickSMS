package Util.Android.MVP

import Util.MVP.Android.Interfaces
import android.content.Context
import com.tinmegali.mvp.mvp.*

abstract class BaseActivity<View : Interfaces.View, PresenterView : Interfaces.PresenterView<View>,
        Presenter : PresenterView> : GenericMVPActivity<View, PresenterView, Presenter>(), Interfaces.View {
    override val ctx : Context by lazy { activityContext }
}