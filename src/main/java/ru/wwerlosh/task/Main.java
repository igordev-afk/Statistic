package ru.wwerlosh.task;


public class Main {
    public static void main( String[] args ) {
//        var group = args[0];
//        var name = args[1];
//        var option = args[2];

        String[] manualArgs = new String[3];
        manualArgs[0] = "ИКПИ-25";
        manualArgs[1] = "Шверло Игорь Сергеевич";
        manualArgs[2] = "53";
        SelectionProcessor.process(manualArgs);
    }

}
