package Util.MVP.Android

import android.content.Context
import com.tinmegali.mvp.mvp.*

/**
 * Created by alex on 12/05/18.
 */
interface Interfaces {
    // Presenter to View
    interface View : ActivityView {
        val ctx : Context
    }
    // View to Presenter
    interface PresenterView<View> : PresenterOps<View>
    // Model to Presenter
    interface PresenterModel {
        val ctx : Context
    }
    // Presenter to Model
    interface Model<PresenterModel> : ModelOps<PresenterModel>
}