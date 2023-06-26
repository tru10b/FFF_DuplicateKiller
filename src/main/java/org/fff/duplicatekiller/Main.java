package org.fff.duplicatekiller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Введите путь к директории с графическими файлами\nПри необходимости " +
                    "копирования дубликатов введите ключ -copy ");
        } else {
            String pathName = args[0];

            //String pathName = "C:/outputs";
            LinkedList<MetadataParsed> uniqueList = new LinkedList<>(), duplicateList = new LinkedList<>();
            ArrayList<Path> pathsArray = new ArrayList<>();

            try (Stream<Path> paths = Files.walk(Paths.get(pathName))) {
                paths.forEach(path -> proceedFile(path.toFile(), uniqueList, duplicateList));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Обнаружено дубликатов:");
            System.out.println(duplicateList.size());

            if (args.length > 1 && args[1].equals("-copy")) {
                new File(pathName + "/duplicates").mkdirs();
                for (MetadataParsed temp :
                        duplicateList) {
                    Path source = Paths.get(temp.filePath);
                    Path newdir = Paths.get(pathName + "/duplicates");
                    try {
                        Files.move(source, newdir.resolve(source.getFileName()), REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Дубликаты перемещены в папку " + pathName + "/duplicates");
            }
        }
    }

    public static void proceedFile(File file, LinkedList<MetadataParsed> uniqueList,
                                   LinkedList<MetadataParsed> duplicateList) {
        if (!file.isDirectory()) {
            String metaData = MetadataUtils.getMetadataToString(file);

            if (!metaData.equals("EXIF metadata is absent")) {
                MetadataParsed temp = new MetadataParsed(file.getAbsolutePath(), MetadataUtils.parse(metaData));

                if (uniqueList.size() == 0) {
                    uniqueList.add(temp);
                } else {
                    boolean isUnique = true;
                    for (MetadataParsed tempMetadataParsed :
                            uniqueList) {
                        if (MetadataUtils.isEqualMetadata(tempMetadataParsed.metaParsed, temp.metaParsed)) {
                            duplicateList.add(temp);
                            isUnique = false;
                            break;
                        }
                    }
                    if (isUnique) {
                        uniqueList.add(temp);
                    }
                }
            }
        }
    }
}