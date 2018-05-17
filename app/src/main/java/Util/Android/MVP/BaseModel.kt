package Util.MVP.Android

import com.tinmegali.mvp.mvp.*

abstract class BaseModel<PresenterModel : Interfaces.PresenterModel>
    : GenericModel<PresenterModel>(), Interfaces.Model<PresenterModel> {
    protected val ctx by lazy { presenter.ctx }
}