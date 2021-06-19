package by.k19.jobpicker.model;

import by.k19.jobpicker.model.strategy.HHStrategy;
import by.k19.jobpicker.model.strategy.HabrCareerStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregator {
    private Provider[] providers;

    public Aggregator(Provider... providers) {
        this.providers = providers;
    }

    private Map<String, Integer> logs = new HashMap<>();

    public List<Vacancy> select(String city) {
        List<Vacancy> vacancies = new ArrayList<>();
        if (providers == null || providers.length == 0)
            return new ArrayList<>();
        for (Provider provider : providers) {
            List<Vacancy> gotVacancies = provider.getJavaVacancies(city);
            vacancies.addAll(gotVacancies);
            logs.put(provider.getSiteName(), gotVacancies.size());
        }
        printLogs();
        return vacancies;
    }

    private void printLogs() {
        for (String site : logs.keySet()) {
            System.out.println("С сайта \"" + site + "\" получено " + logs.get(site) + " вакансий");
        }
    }

    public static Aggregator create() {
        return new Aggregator(
                new Provider(new HHStrategy()),
                new Provider(new HabrCareerStrategy())
        );
    }
}
