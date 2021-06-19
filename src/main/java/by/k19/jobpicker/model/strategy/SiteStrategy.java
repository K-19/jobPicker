package by.k19.jobpicker.model.strategy;

import by.k19.jobpicker.model.Vacancy;

import java.util.List;

public interface SiteStrategy {
    int davnostDay = 3;
    int countPages = 3;
    List<Vacancy> getVacancies(String searchString);
    String getSiteName();
}
