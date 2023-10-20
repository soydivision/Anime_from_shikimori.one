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

    int NUMBER_OF_PAGINATION_PAGES = 214;

    @Test
    public void openShiki() throws IOException {
        Configuration.proxyEnabled = true;
        Configuration.fileDownload = PROXY;
        open(BASE_URL);
        for (int i = 0; i < NUMBER_OF_PAGINATION_PAGES; i++) {
            parsePageWithPageNumber(i);
            clickForward();
        }
    }

    private void clickForward() {
        nextPageButton.click();
    }

    public void parsePageWithPageNumber(int pageNumber) throws IOException {
        String currentURL = WebDriverRunner.getWebDriver().getCurrentUrl();
        appendToLogTxtFile(currentURL);
        appendToLogTxtFile("Number of anime found on page: " + animeList.size());
        appendToLogTxtFile("Number of anime cover links found on page: " + animeCoverLinks.size());
        checkListSizeOnGiveURL(animeList, currentURL);
        checkListSizeOnGiveURL(animeCoverLinks, currentURL);
        int tempPageNumber = pageNumber + 1;
        createAndAppendAnimeListToFile(String.valueOf(tempPageNumber));
        saveAnimeCovers(pageNumber + 1);
        //pageBottom.scrollIntoView(false);
    }

    public void createAndAppendAnimeListToFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        for (int i = 0; i < animeList.size(); i++) {
            System.out.println(animeList.get(i).innerText());
            appendToTxtFile(animeList.get(i).innerText(), fileName);
        }
    }

    public void appendToTxtFile(String txt, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        try {
            String filename = "test-result/list Of Anime For Page" + fileName + ".txt";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(txt);
            fw.write("\n");
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public void checkListSizeOnGiveURL(ElementsCollection elements, String url) throws FileNotFoundException, UnsupportedEncodingException {
        if (elements.size() != 20) {
            appendToLogTxtFile("WARNING: expected  number on page:" + url + " didn't match");
        }
    }

    private void saveAnimeCovers(int pageNumber) throws IOException {
        for (int k = 0; k < animeCoverLinks.size(); k++) {
            String coverFileName = animeCoverLinks.get(k).getAttribute("alt").replaceAll("[\\\\/:*?\"<>|]", "");
            var src = animeCoverLinks.get(k).getAttribute("src");
            var imgFile = new File("test-result/covers/" + pageNumber + "/" + coverFileName + ".jpeg");
            copyURLToFile(new URL(src), imgFile);
        }
    }

    public void parsePage() throws IOException {
        String currentURL = WebDriverRunner.getWebDriver().getCurrentUrl();
        appendToLogTxtFile(currentURL);
        //callScrollDownTimes(25);
        appendToLogTxtFile("Number of anime found on page: " + animeList.size());
        if (animeList.size() != 20) {
            appendToLogTxtFile("WARNING: expected files number on page:" + currentURL + " didn't match");
        }
        appendToLogTxtFile("Number of anime cover links found on page: " + animeCoverLinks.size());
        appendAnimeListToFile();
        saveAnimeCovers();
        pageBottom.scrollIntoView(false);
    }

    private void saveAnimeCovers() throws IOException {
        for (int i = 0; i < animeCoverLinks.size(); i++) {
            String coverFileName = animeCoverLinks.get(i).getAttribute("alt").replaceAll("[\\\\/:*?\"<>|]", "");
            var src = animeCoverLinks.get(i).getAttribute("src");
            var imgFile = new File("test-result/covers/" + coverFileName + ".jpeg");
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
        element.sendKeys(Keys.END);
        sleep(2000);
        element.sendKeys(Keys.PAGE_UP);
        sleep(1000);
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

    public void appendToLogTxtFile(String txt) throws FileNotFoundException, UnsupportedEncodingException {
        try {
            String filename = "test-result/log.txt";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(txt);
            fw.write("\n");
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}
