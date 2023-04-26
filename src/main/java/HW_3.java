//Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
//Фамилия Имя Отчество датарождения номертелефона пол
//Форматы данных:
//фамилия, имя, отчество - строки
//
//дата_рождения - строка формата dd.mm.yyyy
//
//номер_телефона - целое беззнаковое число без форматирования
//
//пол - символ латиницей f или m.
//
//Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым,
// вернуть код ошибки, обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.
//
//Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры.
// Если форматы данных не совпадают, нужно бросить исключение, соответствующее типу проблемы.
// Можно использовать встроенные типы java и создать свои.
// Исключение должно быть корректно обработано, пользователю выведено сообщение с информацией, что именно неверно.
//
//Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии,
// в него в одну строку должны записаться полученные данные, вида
//
//<Фамилия><Имя><Отчество><датарождения> <номертелефона><пол>
//
//Однофамильцы должны записаться в один и тот же файл, в отдельные строки.
//
//Не забудьте закрыть соединение с файлом.
//
//При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано,
// пользователь должен увидеть стектрейс ошибки.

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class HW_3 {
    public static void main(String[] args) {
        MyTuple<int[], String[]> result = null;

        try {
            result = parser(input_data());
        } catch (MyException e) {
            System.out.println(e.getInfo());
        }

        if (result != null) {
            try {
                rec_file(result);
            } catch (IOException e) {
                System.out.println("Ошибка записи в файл " + e.getClass().getSimpleName());

            }

        }
    }


    public static void rec_file(MyTuple<int[], String[]> data) throws IOException {
        int[] pos_el = data.get1();
        String[] strArray = data.get2();
        Path path = Path.of(strArray[pos_el[0]]);

        String content = String.format("<%s><%s><%s><%s><%s><%s>\n",
                strArray[pos_el[0]], strArray[pos_el[1]], strArray[pos_el[2]], strArray[pos_el[3]], strArray[pos_el[4]], strArray[pos_el[5]]);

        Files.writeString(path, content,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

        System.out.println("Файл " + path + " успешно записан");

    }

    public static String input_data() {
        System.out.println("""
                Введите следующие данные в произвольном порядке через пробел:
                'Фамилия Имя Отчество датарождения номертелефона пол'
                фамилия, имя, отчество - строки
                дата_рождения - строка формата dd.mm.yyyy
                номер_телефона - целое беззнаковое число без форматирования
                пол - символ латиницей f или m""");
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public static MyTuple<int[], String[]> parser(String data) throws MyException {
        String[] strArray = data.split(" ");
        if (strArray.length == 6) {
            System.out.println("Введено верное количество даных");

            int[] pos_el = new int[]{-1, -1, -1, -1, -1, -1}; // 0-фамилия, 1-имя, 2-отчество, 3-датарождения 4-номертелефона 5-пол

            pos_el[3] = search_date(strArray);
            if (pos_el[3] == -1)
                throw new MyException("Не найдено поле с датой");

            pos_el[4] = search_tel(strArray);
            if (pos_el[4] == -1)
                throw new MyException("Не найдено поле с номером телефона");

            pos_el[5] = search_gender(strArray);
            if (pos_el[5] == -1)
                throw new MyException("Не найдено поле с полом");

            pos_el[0] = search_string(strArray, pos_el);
            if (pos_el[0] == -1)
                throw new MyException("Не найдено поле с Фамилией");

            pos_el[1] = search_string(strArray, pos_el);
            if (pos_el[1] == -1)
                throw new MyException("Не найдено поле с Именем");

            pos_el[2] = search_string(strArray, pos_el);
            if (pos_el[2] == -1)
                throw new MyException("Не найдено поле с Отчеством");
            return new MyTuple<>(pos_el, strArray);


        } else if (strArray.length > 6) {
            throw new MyException("Количество введенных данных больше допустимого");
        } else throw new MyException("Количество введенных данных меньше требуемого");

    }


    public static int search_date(String[] strArray) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < 6; i++) {
            try {
                df.parse(strArray[i]);
                return i;
            } catch (ParseException ignored) {
            }
        }
        return -1;
    }

    public static int search_tel(String[] strArray) {
        for (int i = 0; i < 6; i++) {
            try {
                Integer.parseInt(strArray[i]);
                return i;
            } catch (NumberFormatException ignored) {
            }
        }
        return -1;
    }

    public static int search_gender(String[] strArray) {

        for (int i = 0; i < 6; i++) {
            if (strArray[i].equals("f") || strArray[i].equals("m"))
                return i;
        }
        return -1;
    }

    public static int search_string(String[] strArray, int[] pos_el) {

        for (int i = 0; i < 6; i++) {
            boolean cont = false;
            for (int k : pos_el) {
                if (i == k) {
                    cont = true;
                    break;
                }
            }
            if (cont) continue;
            if (!strArray[i].isEmpty() && strArray[i].chars().allMatch(Character::isLetter))
                return i;
            else return -1;
        }
        return -1;
    }

    public static final class MyTuple<A, B> {
        private final A v1;
        private final B v2;

        public MyTuple(A v1, B v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public A get1() {
            return v1;
        }

        public B get2() {
            return v2;
        }
    }


    public static class MyException extends RuntimeException {

        public MyException(String message) {
            super(message);
            m = "Ошибка ввода данных -> " + message;
        }

        private final String m;

        public String getInfo() {
            return m;
        }
    }
}