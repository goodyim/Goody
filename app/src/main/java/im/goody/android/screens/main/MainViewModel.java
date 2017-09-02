package im.goody.android.screens.main;

import java.util.ArrayList;
import java.util.List;

class MainViewModel {
    private List<MainItemViewModel> data;
    private int page;

    MainViewModel() {
        page = 1;
    }


    public List<MainItemViewModel> getData() {
        return data == null ? null : new ArrayList<>(data);
    }

    void setData(List<MainItemViewModel> data) {
        this.data = new ArrayList<>(data);
    }

    void addData(List<MainItemViewModel> additionalData) {
        data.addAll(additionalData);
    }

    int incrementPageAndGet() {
        page++;
        return page;
    }

    int resetPageAndGet() {
        page = 1;
        return page;
    }
}
