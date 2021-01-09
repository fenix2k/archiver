# Archiver

Консольное Java приложение, выполняющее функцию архивирования/разархивирования одного или несколько файлов или папок.
Архив создётся в формате ZIP.

### Требования
Java Runtime Enviroment 11
Оперционная система: Linux, MacOS.

### Процесс сборки

Для компиляции приложения Archiver требуется JDK 11 или выше и maven версии 2 и выше.
В процессе компиляции и сборки производится юнит-тестирование класса Archiver.
```sh
$ git clone https://github.com/fenix2k/archiver
$ cd ./archiver
$ mvn clean package
```
Скомпилированные файл будет находится в
./target/archiver-1.0-jar-with-dependencies.jar

### Запуск приложения
##### Архивация данных:
Исходные файлы для архивации должны быть указаны в качестве параметром запуска программы через пробел.
```sh
java -jar ./target/archiver-1.0-jar-with-dependencies.jar <dir or filename> [<dir or filename>] > <destination filename>
```
##### Распаковка:
Распакова производиться в текущую директорию.
```sh
cat <source filename> | java -jar ./target/archiver-1.0-jar-with-dependencies.jar
```