package pl.put.poznan.whereismymoney.controllers.generalsubviews;

import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.model.Budget;

public interface GeneralSubviewController extends Controller {
    void setSelectedBudget(Budget selectedBudget);
    void setGeneralViewController(Controller mainController);
}
