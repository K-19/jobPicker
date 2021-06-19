package by.k19.jobpicker.model.strategy;

import by.k19.jobpicker.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HHStrategy implements SiteStrategy {
    private static final Properties properties = new Properties();
    static {
        try {
            FileReader reader = new FileReader("tags.properties");
            properties.load(reader);
        } catch (IOException e ){
            e.printStackTrace();
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(properties.getProperty("hh.dateFormat"));
    private static final List<String> VACANCIES_TAGS = new ArrayList<>();
    static {
        VACANCIES_TAGS.add(properties.getProperty("hh.vacTag1"));
        VACANCIES_TAGS.add(properties.getProperty("hh.vacTag2"));
        VACANCIES_TAGS.add(properties.getProperty("hh.vacTag3"));
    }

    @Override
    public String getSiteName() {
        return properties.getProperty("hh.siteName");
    }

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> allVacancies = new ArrayList<>();

        int page = 0;
        try {
            do {
                boolean continueSearch = false;
                Document doc = getDocument(searchString, page);
                String dataTag = properties.getProperty("hh.dataTag");
                for (String vacanciesTag : VACANCIES_TAGS) {
                    Elements vacanciesHtmlList = doc.getElementsByAttributeValue(dataTag, vacanciesTag);
                    if(vacanciesHtmlList.isEmpty())
                        break;
                    else continueSearch = true;
                    for (Element element : vacanciesHtmlList) {
                        Elements links = element.getElementsByAttributeValue(dataTag, properties.getProperty("hh.vacTitle"));
                        Elements locations = element.getElementsByAttributeValue(dataTag, properties.getProperty("hh.vacAddress"));
                        Elements companyName = element.getElementsByAttributeValue(dataTag, properties.getProperty("hh.vacEmployer"));
                        Elements salary = element.getElementsByAttributeValue(dataTag, properties.getProperty("hh.vacSalary"));
                        Elements dates = element.getElementsByClass(properties.getProperty("hh.vacDate"));

                        LocalDate date = LocalDate.parse(
                                dates.get(0).text() + "." + Integer.toString(LocalDateTime.now().getYear()), formatter);
                        if(date.isBefore(date.minusDays(davnostDay)))
                            continue;

//                        if (!locations.get(0).text().contains("Минск")) //TODO: Поиск по Минску
//                            continue;
                        Vacancy vacancy = new Vacancy();
                        vacancy.setSiteName(properties.getProperty("hh.siteName"));
                        vacancy.setSiteLogo(properties.getProperty("hh.logoSiteUrl"));
                        vacancy.setTitle(links.get(0).text());
                        vacancy.setUrl(links.get(0).attr("href"));
                        vacancy.setCity(locations.get(0).text());
                        vacancy.setCompanyName(companyName.get(0).text());
                        vacancy.setSalary(salary.size() > 0 ? salary.get(0).text() : "");
                        vacancy.setPublishTime(date);

                        allVacancies.add(vacancy);
                        System.out.printf(properties.getProperty("logs.newVac"), properties.getProperty("hh.siteName"), links.get(0).text());
                    }
                }
                if(!continueSearch)
                    break;
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
        return Jsoup.connect(String.format(properties.getProperty("hh.urlFormat"), searchString, page))
                .userAgent(properties.getProperty("browser.userAgent"))
                .referrer(properties.getProperty("hh.referrer"))
                .get();
    }
}
