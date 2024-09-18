package bettingprosscraper;

import org.example.Arbitrage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

public class Main {
    public static final String BASE_URL = "https://www.bettingpros.com/nfl/odds/player-props/";
    public static Integer PRIZEPICKS_INDEX = 3;
    public static Integer BET365_INDEX = 6;
//    public static Integer UNDERDOG_INDEX = 11;
    public static void main(String[] args) throws IOException {
        List<Bet> bets = new ArrayList<>();
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\benne\\OneDrive\\Desktop\\geckodriver-v0.35.0-win64\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(BASE_URL);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        List<String> urls = driver.findElements(By.className("player-card__link")).stream().map(url -> url.getAttribute("href")).toList();
        for (String url : urls) {
            try {
                driver.get(url);
                WebElement x = driver.findElement(By.className("odds-market-label"));
                x.click();
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                List<WebElement> markets = driver.findElements(By.className("grouped-items-with-sticky-footer__content"));
                for (WebElement market : markets) {
                    Document html = Jsoup.parse(market.getAttribute("innerHTML"));
                    String name = html.select("span.odds-market-label").text();
                    Integer y = html.select("div.flex.odds-offer__item").size()-3;
//                    Integer y = PRIZEPICKS_INDEX;
                    String prizePicksOdds = html.select("div.flex.odds-offer__item").get(y).text();
                    String bet365Odds = html.select("div.flex.odds-offer__item").get(BET365_INDEX).text();
                    if (!prizePicksOdds.contains("NL") && !prizePicksOdds.contains("OFF") &&
                            !bet365Odds.contains("NL") && !bet365Odds.contains("OFF")) {
                        String prizePicksLine = prizePicksOdds.split("\\(")[0].split(" ")[1];
                        String bet365Line = bet365Odds.split("\\(")[0].split(" ")[1];
                        if (Objects.equals(prizePicksLine, bet365Line)) {
                            String bet365Over = bet365Odds.split("\\(")[1].split("\\)")[0].replace("EVEN", "+100");
                            String bet365Under = bet365Odds.split("\\(")[2].split("\\)")[0].replace("EVEN", "+100");
                            bets.add(new Bet(true, name, url.replace(BASE_URL, ""), Double.parseDouble(bet365Line), Double.parseDouble(prizePicksLine), Double.parseDouble(bet365Over), Double.parseDouble(bet365Under)));
                        }
                        else {
                            String bet365Over = bet365Odds.split("\\(")[1].split("\\)")[0].replace("EVEN", "+100");
                            String bet365Under = bet365Odds.split("\\(")[2].split("\\)")[0].replace("EVEN", "+100");
                            if((Integer.parseInt(bet365Under) > 0 && Double.parseDouble(prizePicksLine) < Double.parseDouble(bet365Line)) ||
                                    (Integer.parseInt(bet365Over) > 0 && Double.parseDouble(bet365Line) < Double.parseDouble(prizePicksLine))) {
                                bets.add(new Bet(false, name, url.replace(BASE_URL, ""), Double.parseDouble(bet365Line), Double.parseDouble(prizePicksLine), Double.parseDouble(bet365Over), Double.parseDouble(bet365Under)));
                            }
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        List<Arbitrage> arbs = new ArrayList<>();
        bets.stream().filter(bet -> !bet.getSameLine()).forEach(bet -> {
            arbs.add(bet.getOverArbitrage());
            arbs.add(bet.getUnderArbitrage());
        });
        arbs.stream().sorted().forEach(Arbitrage::print);

        System.out.println("\n\n\n\n");

        bets.stream().filter(Bet::getSameLine).forEach(bet -> {
            arbs.add(bet.getOverArbitrage());
            arbs.add(bet.getUnderArbitrage());
        });
        arbs.stream().sorted().forEach(Arbitrage::print);
        driver.quit();

    }
}
