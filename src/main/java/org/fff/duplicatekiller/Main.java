package org.fff.duplicatekiller;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.fff.duplicatekiller.MetadataUtils.getMetadataToString;
import static org.fff.duplicatekiller.MetadataUtils.parse;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if (args.length == 0) {
            System.out.println("Введите путь к директории с графическими файлами\nПри необходимости " +
                    "копирования дубликатов введите ключ -copy ");
        } else {
            String pathName = args[0];
            List<MetadataParsed> uniqueList, fullList, duplicateList;

            try (Stream<Path> paths = Files.walk(Paths.get(pathName))) {
                fullList = paths.
                        map(path -> {
                            if (!path.toFile().isDirectory()) {
                                String metaData = getMetadataToString(path.toFile());
                                if (!metaData.equals("EXIF metadata is absent")) {
                                    return new MetadataParsed(path.toFile().getAbsolutePath(), parse(metaData));
                                }
                            }
                            return null;
                        }).
                        filter(Objects::nonNull).toList();
                uniqueList = fullList.parallelStream().unordered().distinct().toList();
                duplicateList = (List<MetadataParsed>) CollectionUtils.subtract(fullList, uniqueList);

                System.out.println("Обнаружено дубликатов:");
                System.out.println(duplicateList.size());
                if (args.length > 1 && args[1].equals("-copy")) {
                    moveDuplicateFiles(pathName, duplicateList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время работы " + (endTime - startTime) + " миллисекунды");
    }

    public static void moveDuplicateFiles(String pathName, List<MetadataParsed> duplicateList) {
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