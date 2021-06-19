package by.k19.jobpicker.model.strategy;

import by.k19.jobpicker.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerStrategy implements SiteStrategy {

    private static final String SITE_NAME = "career.habr.com";
    private static final String URL_FORMAT = "https://career.habr.com/vacancies?q=java+%s&page=%d";
    private static final String DATA_TAG = "vacancy-card";
    private static final String VACANCY_TITLE = "vacancy-card__title";
    private static final String VACANCY_ADDRESS = "vacancy-card__meta";
    private static final String VACANCY_EMPLOYER = "vacancy-card__company-title";
    private static final String VACANCY_SALARY = "basic-salary";
    private static final String VACANCY_DATE = "vacancy-card__date";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2021-05-19
    private static final String LOGO_SITE_URL = "https://upload.wikimedia.org/wikipedia/ru/thumb/7/7f/Habrahabr_logo.png/136px-Habrahabr_logo.png";
    private static final List<String> VACANCIES_TAGS = new ArrayList<>();
    static {
        VACANCIES_TAGS.add("vacancy-serp__vacancy vacancy-serp__vacancy_standard");
        VACANCIES_TAGS.add("vacancy-serp__vacancy vacancy-serp__vacancy_standard_plus");
        VACANCIES_TAGS.add("vacancy-serp__vacancy vacancy-serp__vacancy_premium");
    }

    @Override
    public String getSiteName() {
        return SITE_NAME;
    }

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> allVacancies = new ArrayList<>();

        int page = 0;
        try {
            do {
                Document doc = getDocument(searchString, page);

                Elements vacanciesHtmlList = doc.getElementsByClass(DATA_TAG);

                if (vacanciesHtmlList.isEmpty()) break;

                for (Element element : vacanciesHtmlList) {
                    Elements title = element.getElementsByClass(VACANCY_TITLE);
                    Elements links = title.get(0).getElementsByTag("a");
                    Elements meta = element.getElementsByClass(VACANCY_ADDRESS);
                    Elements locations = meta.get(0).getElementsByClass("preserve-line");
                    List<String> places = new ArrayList<>();
                    for (Element location : locations) {
                        Elements as = location.getElementsByTag("a");
                        if (as.size() != 0)
                            places.add(as.get(0).text());
                    }
                    Elements companyName = element.getElementsByClass(VACANCY_EMPLOYER);
                    Elements salary = element.getElementsByClass(VACANCY_SALARY);
                    Elements dates = element.getElementsByClass(VACANCY_DATE);
                    LocalDate date = LocalDate.parse(dates.get(0).getElementsByTag("time").get(0).attr("datetime").substring(0, 10), formatter);
                    if(date.isBefore(date.minusDays(davnostDay)))
                        continue;


                    if (!links.get(0).text().contains("Java"))
                        continue;

//                    if (!places.contains("Минск")) //TODO: Поиск по Минску
//                        continue;

                    Vacancy vacancy = new Vacancy();
                    vacancy.setSiteName(SITE_NAME);
                    vacancy.setTitle(links.get(0).text());
                    vacancy.setSiteLogo(LOGO_SITE_URL);
                    vacancy.setUrl("https://career.habr.com" + links.get(0).attr("href"));
                    vacancy.setCity(String.join(", ", places.toArray(new String[]{})));
                    vacancy.setCompanyName(companyName.get(0).text());
                    vacancy.setSalary(salary.size() > 0 ? salary.get(0).text() : "");
                    vacancy.setPublishTime(date);

                    allVacancies.add(vacancy);
                    System.out.println("Добавлена вакансия с сайта career.habr.com: " + links.get(0).text());
                }

                page++;
                if (page >= countPages)
                    break;
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allVacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                .referrer("https://career.habr.com/")
                .get();
    }
}
