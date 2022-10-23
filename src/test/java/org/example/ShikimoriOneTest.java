package org.example;

import com.codeborne.selenide.*;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;

import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.*;
import static org.apache.commons.io.FileUtils.copyURLToFile;

public class ShikimoriOneTest {

    SelenideElement element = element(Selectors.byXpath("/html"));
    String BASE_URL = "https://shikimori.one/animes/kind/movie";
    SelenideElement pageBottom = element(Selectors.byXpath("//div[@class='copyright']"));
    SelenideElement nextPageButton = element(Selectors.byXpath("//a[@class='link link-next']"));
    ElementsCollection animeList = Selenide.elements(Selectors.byXpath("//span[@class='name-en']"));
    ElementsCollection animeCoverLinks = Selenide.elements(Selectors.byXpath("//article//img"));


    @Test
    public void openShiki() throws IOException {
        Configuration.proxyEnabled = true;
        Configuration.fileDownload = PROXY;
        open(BASE_URL);
        for (int i = 0; i < 13; i++) {
            parsePage();
            clickForward();
        }
    }

    private void clickForward() {
        nextPageButton.click();
    }

    public void parsePage() throws IOException {
        String currentURL = WebDriverRunner.getWebDriver().getCurrentUrl();
        appendToTxtFile(currentURL);
        callScrollDownTimes(25);
        appendToTxtFile("Number of anime found on page: " + animeList.size());
        appendAnimeListToFile();
        saveAnimeCovers();
        pageBottom.scrollIntoView(false);
    }

    private void saveAnimeCovers() throws IOException {
        for (int i = 0; i < animeCoverLinks.size(); i++) {
            String name = animeCoverLinks.get(i).getAttribute("alt").replaceAll("[\\\\/:*?\"<>|]", "");
            var src = animeCoverLinks.get(i).getAttribute("src");
            var imgFile = new File("test-result/covers/" + name + ".jpg");
            copyURLToFile(new URL(src), imgFile);
        }
    }

    public void callScrollDownTimes(int times) {
        for (int i = 0; i < times; i++) {
            scrollDown();
        }
    }

    public void appendAnimeListToFile() throws FileNotFoundException, UnsupportedEncodingException {
        for (int i = 0; i < animeList.size(); i++) {
            System.out.println(animeList.get(i).innerText());
            appendToTxtFile(animeList.get(i).innerText());
        }
    }

    public void scrollDown() {
        element.sendKeys(Keys.PAGE_DOWN);
        sleep(300);
        element.sendKeys(Keys.PAGE_UP);
        sleep(300);
        element.sendKeys(Keys.PAGE_DOWN);
    }

    public void appendToTxtFile(String txt) throws FileNotFoundException, UnsupportedEncodingException {
        try {
            String filename = "test-result/anime_list.txt";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(txt);
            fw.write("\n");
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}
