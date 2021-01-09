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
$ cd <path-to>/archiver
$ mvn clean package
```

### Запуск приложения
##### Архивация данных:
Исходные файлы для архивации должны быть указаны в качестве параметром запуска программы через пробел.
```sh
java -jar ./archiver <dir or filename> [<dir or filename>] > <destination filename>
```
##### Распаковка:
Распакова производиться в текущую директорию.
```sh
cat <source filename> | java -jar ./archiver
```