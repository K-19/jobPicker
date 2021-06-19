package by.k19.jobpicker.model;

import by.k19.jobpicker.model.strategy.SiteStrategy;

import java.util.List;

public class Provider {
    private SiteStrategy strategy;

    public Provider(SiteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SiteStrategy strategy) {
        this.strategy = strategy;
    }

    public String getSiteName() {
        return strategy.getSiteName();
    }

    public List<Vacancy> getJavaVacancies(String searchString){
        return strategy.getVacancies(searchString);
    }
}
