package cn.qiuyiping.tensorflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mnist {

    private static int size = 0;
    private static int images = 0;

    public static void main(String[] args) {

        List<Integer> labelList = readLabel("E:\\tensorflow\\mnist\\train-labels.idx1-ubyte");
        List<byte[]> pixelList = readPixel("E:\\tensorflow\\mnist\\train-images.idx3-ubyte");

        Map<Integer, Float[]> trainMap = new HashMap<>();
        Map<Integer, Integer> numCount = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Float[] values = new Float[size];
            for (int k = 0; k < size; k++) {
                values[k] = 0f;
            }
            trainMap.put(i, values);
            numCount.put(i, 0);
        }

        for (int i = 0; i < pixelList.size(); i++) {
            byte[] bytes = pixelList.get(i);
            int num = labelList.get(i);
            Float[] values = trainMap.get(num);
            for (int k = 0; k < size; k++) {
                values[k] += getUnsignedByte(bytes[k]);
            }
            trainMap.put(num, values);
            numCount.put(num, numCount.get(num) + 1);
        }

        for (int i = 0; i < 10; i++) {
            Float[] values = trainMap.get(i);
            for (int k = 0; k < size; k++) {
                values[k] = values[k] / numCount.get(i);
            }
            trainMap.put(i, values);
        }

        System.out.println("Train over!");

        int successCount = 0;
        List<Integer> testLabelList = readLabel("E:\\tensorflow\\mnist\\t10k-labels.idx1-ubyte");
        List<byte[]> testPixelList = readPixel("E:\\tensorflow\\mnist\\t10k-images.idx3-ubyte");
        for(int j = 0; j < images; j ++) {
            byte[] tt = testPixelList.get(j);
            double[] results = new double[10];
            for (int i = 0; i < 10; i++) {
                double sum = 0d;
                Float[] values = trainMap.get(i);
                for (int k = 0; k < size; k++) {
                    double drt = values[k] - getUnsignedByte(tt[k]);
                    sum += Math.pow(drt, 2);
                }
                results[i] = sum;
            }
            int smallIndex = 0;
            for (int i = 1; i < 10; i++) {
                if (results[i] < results[smallIndex]) {
                    smallIndex = i;
                }
            }
//            System.out.println(smallIndex);
            if(smallIndex == testLabelList.get(j)){
                successCount ++;
            }
        }
        System.out.println("准确率： " + ((successCount + 0d)/images));
        System.out.println("over");

    }

    private static List<byte[]> readPixel(String filePath) {
        List<byte[]> list = new ArrayList<>(60000);
        try {
            byte[] labels = Files.readAllBytes(Paths.get(filePath));
            System.out.println(labels.length);

            byte[] magic = new byte[4];
            System.arraycopy(labels, 0, magic, 0, 4);
            System.out.println(bytesToInt(magic));

            byte[] length = new byte[4];
            System.arraycopy(labels, 4, length, 0, 4);
            System.out.println(bytesToInt(length));
            images = bytesToInt(length);

            byte[] rows = new byte[4];
            System.arraycopy(labels, 8, rows, 0, 4);
            System.out.println(bytesToInt(rows));

            byte[] columns = new byte[4];
            System.arraycopy(labels, 12, columns, 0, 4);
            System.out.println(bytesToInt(columns));

            size = bytesToInt(rows) * bytesToInt(columns);

            byte[] nums = new byte[labels.length - 16];
            System.arraycopy(labels, 16, nums, 0, labels.length - 16);

            byte[] imageBytes;
            for (int i = 0; i < images; i++) {
                imageBytes = new byte[size];
                System.arraycopy(labels, 16 + size * i, imageBytes, 0, size);
                list.add(imageBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }

    private static int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF;
    }

    private static List<Integer> readLabel(String filePath) {
        List<Integer> list = new ArrayList<>(60000);
        try {
            byte[] labels = Files.readAllBytes(Paths.get(filePath));
            System.out.println(labels.length);
            byte[] magic = new byte[4];
            System.arraycopy(labels, 0, magic, 0, 4);
            byte[] length = new byte[4];
            System.arraycopy(labels, 4, length, 0, 4);
            byte[] nums = new byte[labels.length - 8];
            System.arraycopy(labels, 8, nums, 0, labels.length - 8);
            for (byte num : nums) {
                list.add((int) num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
