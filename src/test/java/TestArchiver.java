import org.apache.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Класс для тестирования методов класса Archiver.
 * @autor Alexander Kuznetsov
 * @version 1.0
 */
class TestArchiver {
  /**
   * Поле для осуществления логирования работы приложения. Логирование ведётся в файле.
   * Настройки логера resources/log4j.properties
   * */
  private static final Logger LOGGER = Logger.getLogger(TestArchiver.class);

  /** Список исходных проверочных файлов/папок */
  private List<File> originalFiles;
  /** Заархивированный файл */
  private File outputFile;
  /** Заархивированный файл */
  private final File testArchiveFile = new File("./_test/test-archive.zip");

  /**
   *  Метод запускается перед каждым тестом
   */
  @BeforeEach
  public void initTest() {
    LOGGER.info("--====== Init Tests ======--");
    this.originalFiles = new ArrayList<>(Arrays.asList(
        new File("./_test/testData/"),
        new File("./_test/testData0/"),
        new File("./_test/testData1"),
        new File("./_test/recovery.img")
    ));
    this.outputFile = new File("./output.zip");
  }

  /**
   *  Метод запускается после каждого теста
   */
  @AfterEach
  public void finalizeTest() {
    LOGGER.info("--====== Finalize Tests ======--");
  }

  /**
   *  Метод тестирует метод Archiver.packZip.
   *  Тест проверяет что архив создается и имеет не нулевой размер.
   */
  @Test
  void packZip() {
    try {
      // Invoke Archiver.packZip method to archive files
      Archiver.packZip(originalFiles, new BufferedOutputStream(new FileOutputStream(outputFile)));
      Assertions.assertTrue(outputFile.exists()
          && Files.size(Paths.get(outputFile.getPath())) > 0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Метод тестирует метод Archiver.unpackZip.
   *  Тест по-файлово проверяет распакованные файлы и исходные файлы на совпадение имени и размера.
   */
  @Test
  void unpackZip() {
    try {
      // Invoke Archiver.unpackZip method to extract files and get list of them as result
      String baseDir = "./";
      List<String> unpackedFiles = Archiver.unpackZip(new BufferedInputStream(new FileInputStream(outputFile)), baseDir);

      // Get original list of archived files
      List<String> originalFiles = new ArrayList<>();
      // Use reflection API to invoke private method
      Method method = Archiver.class.getDeclaredMethod("getChildItems", File.class);
      method.setAccessible(true);
      for (File inputFile: this.originalFiles) {
        for (File file : (List<File>) method.invoke(null, inputFile)) {
          originalFiles.add(Paths.get(file.getPath()).normalize().toString());
        }
      }

      // Compare original and extracted files by filename and size
      for (int i=0; i<unpackedFiles.size(); i++) {
        File originalFile = new File(originalFiles.get(i));
        File unpackedFile = new File(unpackedFiles.get(i));
        long originalSize = originalFile.length();
        long unpackedSize = unpackedFile.length();
        Assertions.assertEquals(originalFile.getName(), unpackedFile.getName());
        Assertions.assertTrue(originalSize == unpackedSize);
      }

      // Remove unpacked files and directories
      this.deleteFileList(unpackedFiles, baseDir);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Метод тестирует приватный метод Archiver.getChildItems с импользованием рефлексии.
   *  Тест совпадение результата работы метода с проверочными данными.
   */
  @Test
  void getChildItems() {
    File testItems = new File("_test/testData0");
    List<File> expectedItems = new ArrayList<>(Arrays.asList(
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-2/Application.html"),
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-2/Archiver.html"),
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-2/constant-values.html"),
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-1/allclasses.html"),
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-1/allclasses-index.html"),
        new File("_test/testData0/folderWithSubfolders0/folderWithFiles1-1/allpackages-index.html"),
        new File("_test/testData0/folderWithFiles0/element-list"),
        new File("_test/testData0/folderWithFiles0/index-all.html"),
        new File("_test/testData0/folderWithFiles0/index.html"),
        new File("_test/testData0/folderWithFiles0/deprecated-list.html")
    ));

    try {
      Method method = Archiver.class.getDeclaredMethod("getChildItems", File.class);
      method.setAccessible(true);

      List<File> actualList = (List<File>) method.invoke(null, testItems);

      for (int i = 0; i < expectedItems.size(); i++) {
        File expected = expectedItems.get(i);
        File actual = actualList.get(i);
        Assertions.assertEquals(expected, actual);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *  Вспомогательный метод. Удаляет файлы согласно списку.
   * @param pathList список путей к файлам
   * @param baseDir базовая директория
   */
  private void deleteFileList(List<String> pathList, String baseDir) throws IOException {
    Set<Path> paths = new HashSet<>();
    for (String filePath: pathList) {
      int i = baseDir.length();
      for (; i<filePath.length();i++)
        if(filePath.charAt(i) == File.separator.charAt(0)) break;
      Path path = Paths.get(filePath.substring(0, i));
      paths.add(path);
    }
    for (Path path: paths) {
      Files.walk(path)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }

}