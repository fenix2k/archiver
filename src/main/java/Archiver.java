
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * Класс для создания и распаковки ZIP архивов.
 * @autor Alexander Kuznetsov
 * @version 1.0
 */
public class Archiver {
  /**
   * Поле для осуществления логирования работы приложения. Логирование ведётся в файле.
   * Настройки логера resources/log4j.properties
   * */
  private static final Logger LOGGER = Logger.getLogger(Archiver.class);

  /**
   * Метод получает список inputFiles, определяет полный перечень вложенных файлов и папок, производит их
   * укаковку и сжатие в фомате ZIP затем отправляет в выходной поток outputStream.
   * @param inputFiles - список путей к файлам и директориям, которые должны быть заархивированы.
   * @param outputStream - выходной поток, в который будет записан архив.
   * @return возвращает список файлов List<String> добавленных в архив.
   */
  public static List<String> packZip(List<File> inputFiles, OutputStream outputStream) {
    LOGGER.debug("Creating an archive");

    List<String> archivedFiles = new ArrayList<>();
    byte[] buffer = new byte[2048];

    // Use java.util.zip.ZipOutputStream for archiving
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

      for (File inputFile: inputFiles) {
        Path rootFilePath = Paths.get(inputFile.getPath()).getParent().normalize();
        LOGGER.debug("Root file path is: " + rootFilePath);

        // Get children of input file. If no children the process with the same file
        for (File file: Archiver.getChildItems(inputFile)) {
          // defining the zipEntry name from file path
          Path filePath = Paths.get(file.getPath()).normalize();
          String entryName = rootFilePath.relativize(filePath).normalize().toString();

          // If file is a directory then append "/" to path
          if(file.isDirectory()) {
            entryName = entryName + File.separator;
            LOGGER.debug("is a directory: " + entryName);
          }

          // Set zipEntry name
          ZipEntry zipEntry = new ZipEntry(entryName);
          zipOutputStream.putNextEntry(zipEntry);
          archivedFiles.add(entryName);
          LOGGER.debug("add file: " + entryName);

          if(!zipEntry.isDirectory()) {
            // Write file to output stream
            LOGGER.debug("write file to output stream");
            try (FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath())) {
              int len;
              while ((len = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
              }
            } catch (FileNotFoundException ex) {
              throw new IOException(ex);
            }
          }
        }
      }
      LOGGER.debug("Archive was created");
    }
    catch (IOException ex) {
      System.err.println("Error: " + ex.getMessage());
      LOGGER.debug("Error: " + ex.getMessage());
      System.exit(1);
    }
    return archivedFiles;
  }

  /**
   * Метод получает из входного потока inputStream данные и производит их распаковку в директории outputDir.
   * @param inputStream входной поток, откуда будет получены входные данные для архивации.
   * @param outputDir путь к директории, в которой будет распакован архив.
   * @return возвращает список файлов List<String> извлеченных из архива.
   */
  public static List<String> unpackZip(InputStream inputStream, String outputDir) {
    LOGGER.debug("Unpacking an archive in current directory");
    LOGGER.debug("Output directory: " + outputDir);
    List<String> unpackedFiles = new ArrayList<>();

    // Use java.util.zip.ZipInputStream for archive unpacking
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      ZipEntry zipEntry = null;
      byte[] buffer = new byte[2048];
      LOGGER.debug("Reading stdin pipe");

      // Unpacking the contents of the archive
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        // defining the file name from the archive content
        Path outputFileName = Paths.get(outputDir, zipEntry.getName());
        unpackedFiles.add(outputFileName.toString());
        LOGGER.debug("Unpack file " + outputFileName);

        // If entry is a directory then create a directory on disk (for empty directories)
        // else if a file then create directories and write file to disk
        if (zipEntry.isDirectory()) {
          LOGGER.debug("Is a directory. Create directory: " + outputFileName);
          outputFileName.toFile().mkdirs();
        } else {
          LOGGER.debug("Is a file. Create directory: " + outputFileName);
          outputFileName.getParent().toFile().mkdirs();

          LOGGER.debug("Write to output stream");
          FileOutputStream fileOutputStream = new FileOutputStream(outputFileName.toFile());
          int len;
          while ((len = zipInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len);
          }
        }
      }
    }
    catch (IOException ex) {
      System.err.println("Error: " + ex.getMessage());
      LOGGER.debug("Error: " + ex.getMessage());
    }
    LOGGER.debug("Stdin was not used");
    return unpackedFiles;
  }

  /**
   * Метод возвращает список содержащихся в dir вложенных файлов и папок.
   * Если в качестве входного параметра передал файл, то результатом работы метода будет список из одного этого файла.
   * @param dir исходная папка или файл.
   * @return список вложенных файлов и папок.
   */
  private static List<File> getChildItems(File dir) {
    LOGGER.debug("Scanning directory structure: " + dir.toString());
    
    List<File> allItems = new ArrayList<>();

    if(!dir.isDirectory())
      allItems.add(dir);
    else if(dir.listFiles().length == 0) {
      allItems.add(dir);
    }
    else {
      for (File file : dir.listFiles()) {
        if (file.isFile())
          allItems.add(file);
        else
          allItems.addAll(getChildItems(file));
      }
    }

    return allItems;
  }

}
