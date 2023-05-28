package controller.actions;

import model.*;
import view.*;

public class ShowDescription implements Action {
    private final String xid;

    public ShowDescription(ActionArgs args) {
        this.xid = args.params();
    }

    @Override
    public void run(AllInformation allInformation, SearchView searchView) {
        String description = allInformation.getNearDescription(xid);
        searchView.showDescription(description);
    }
}