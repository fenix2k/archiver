# Archiver

Консольное Java приложение, выполняющее функцию архивирования/разархивирования одного или несколько файлов или папок.\
Архив создётся в формате ZIP.

### Требования
Java Runtime Enviroment 11 \
Оперционная система: Linux, MacOS.

### Процесс сборки

Для компиляции приложения Archiver требуется JDK 11 или выше и maven версии 2 и выше.
```sh
$ git clone https://github.com/fenix2k/archiver
$ cd ./archiver
$ mvn clean package
```
Скомпилированный файл будет находится в\
./target/archiver-1.0-jar-with-dependencies.jar

### Тестирование
В процессе компиляции и сборки (mvn package) производится юнит-тестирование класса Archiver.
Для тестирования создана директория ./_test, содержимое которой используется для архивирования и проверки работы программы.\
Запуск тестирования:
```sh
$ mvn test
```

### Запуск приложения
##### Архивация данных:
Исходные файлы для архивации должны быть указаны в качестве параметров запуска программы через пробел.
```sh
$ java -jar ./target/archiver-1.0-jar-with-dependencies.jar <dir or filename> [<dir or filename>] > <destination filename>
```
Готовый пример:
```sh
$ java -jar ./target/archiver-1.0-jar-with-dependencies.jar "./_test/testData/" "./_test/testData0/" "./_test/testData1" ./_test/recovery.img > zip123.zip
```

##### Распаковка:
Распакова производиться в текущую директорию.
```sh
$ cat ./zip123.zip | java -jar ./target/archiver-1.0-jar-with-dependencies.jar
```

### Документация Javadoc
Создание документации Javadoc
```sh
$ mvn javadoc:javadoc
```
Документация генерируется в директорию \
./target/site/apidocs