package by.k19.jobpicker;

import by.k19.jobpicker.model.Aggregator;
import by.k19.jobpicker.model.Provider;
import by.k19.jobpicker.model.Vacancy;
import by.k19.jobpicker.model.strategy.HHStrategy;
import by.k19.jobpicker.model.strategy.HabrCareerStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class MainController {
    private List<Vacancy> getVacancies(String searchString) {
        Aggregator aggregator = Aggregator.create();
        List<Vacancy> vacancies = aggregator.select(searchString);
        vacancies.sort(new Comparator<Vacancy>() {
            @Override
            public int compare(Vacancy o1, Vacancy o2) {
                return o2.getPublishTime().compareTo(o1.getPublishTime());
            }
        });
        return vacancies;
    }

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("vacancies", getVacancies("Минск"));
        return "vacancies";
    }

    @GetMapping("/search")
    public String search(@RequestParam("searchString") String parameters, Model model) {
        String resultSearchString = parameters.trim().replaceAll(" ", "+");
        model.addAttribute("vacancies", getVacancies(resultSearchString));
        return "vacancies";
    }
}
