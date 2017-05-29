package pl.put.poznan.thesis.controllers.generalsubviews;

import pl.put.poznan.thesis.controllers.Controller;
import pl.put.poznan.thesis.model.Budget;

public interface GeneralSubviewController extends Controller {
    void setSelectedBudget(Budget selectedBudget);
    void setGeneralViewController(Controller mainController);
}
