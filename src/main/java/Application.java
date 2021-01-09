import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Main class
 *
 * @version 1.0
 * @autor Alexander Kuznetsov
 */
public class Application {
  /**
   * Поле для осуществления логирования работы приложения. Логирование ведётся в файле.
   * Настройки логера resources/log4j.properties
   * */
  private static final Logger LOGGER = Logger.getLogger(Application.class);

  /**
   * Главный метод с которого стартует выполнение программы.
   * Программа принимает аргументы из коммандной строки, обрабатываает их.
   * Есть аргументы присутствуют более одного агрумента, то запускается метод создания архива {@link Application#packAction}
   * Иначе вызывается метод распаковки архива {@link Application#unpackAction}
   * @param args входные параметры консоли
   */
  public static void main(String[] args) {
    List<String> filePaths = new ArrayList<>();

    for (String param: args) {
        filePaths.add(param);
    }

    // if no input arg the execute unpack action
    // else execute pack action
    if(filePaths.isEmpty()) {
      if(Application.unpackAction().size() == 0)
        System.err.println("Invalid input params: enter path to files");
    }
    else
      Application.packAction(filePaths);
  }

  /**
   * Метод запускающий архивацию файлов, переданных в качестве параметров приложения
   * @param filePath список путей к файлам и директориям, которые должны быть заархивированы
   */
  public static void packAction(List<String> filePath) {
    LOGGER.debug("Pack action execute");
    List<File> files = new ArrayList<>();
    for (String path: filePath) {
      files.add(new File(path.trim()));
    }

    Archiver.packZip(files, System.out);
  }

  /**
   * Метод запускающий распаковку файла, переданного через stdin
   */
  public static List<String> unpackAction() {
    LOGGER.debug("Unpack action execute");
    return Archiver.unpackZip(System.in, "./"); //Paths.get("").toAbsolutePath().toString()
  }

}
