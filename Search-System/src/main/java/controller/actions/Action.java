package controller.actions;

import model.AllInformation;
import view.SearchView;

public interface Action {
    void run(AllInformation allInformation, SearchView view);
}
