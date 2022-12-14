import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Matrix {
    private ArrayList<ArrayList<Double>> v;

    public double getE(int i, int j) {
        return v.get(i).get(j);
    }

    public void setE(int i, int j, double d) {
        v.get(i).set(j, d);
    }

    public void addMatrix(Matrix matrix) {
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                this.v.get(i).set(j, v.get(i).get(j) + matrix.v.get(i).get(j));
            }
        }
    }

    public void multNum(double d) {
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                this.v.get(i).set(j, v.get(i).get(j) * d);
            }
        }
    }

    public ArrayList<Double> multVec(ArrayList<Double> vec) {
        ArrayList<Double> res = new ArrayList<>();
        for (int i = 0; i < getRows(); i++) {
            double x = 0;
            for (int j = 0; j < getCols(); j++) {
                x += v.get(i).get(j) * vec.get(j);
            }
            res.add(x);
        }
        return res;
    }

    public void setV(Matrix m) {
        this.v = m.v.stream().map(ArrayList::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public int getRows() {
        return v.size();
    }

    public int getCols() {
        return v.get(0).size();
    }

    public Matrix(int n, int m) {
        ArrayList<ArrayList<Double>> u = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ArrayList<Double> ui = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                ui.add((double) 0);
            }
            u.add(ui);
        }
        v = u;
    }

    public Matrix() {
        ArrayList<ArrayList<Double>> u = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(), m = sc.nextInt();

        for (int i = 0; i < n; i++) {
            ArrayList<Double> ui = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                double x = sc.nextDouble();
                ui.add(x);
            }
            u.add(ui);
        }
        v = u;
    }

    public Matrix(Matrix m) {
        this.setV(m);
    }

    private int getPvt(int col) throws Exception {
        for (int i = col; i < this.getRows(); i++) {
            if (v.get(i).get(col) != 0) return i;
        }
        throw new Exception();
    }

    private void rowsSwap(int j, int i) {
        ArrayList<Double> tmp = new ArrayList<>();
        tmp = v.get(i);
        v.set(i, v.get(j));
        v.set(j, tmp);
    }

    public void rowsAdd(int j, ArrayList<Double> u) {
        for (int k = 0; k < u.size(); k++) {
            v.get(j).set(k, v.get(j).get(k) + u.get(k));
        }
    }

    public void print() {
        for (int i = 0; i < getRows(); i++) {
            System.out.print("[");
            for (int j = 0; j < getCols(); j++) {
                if (j == getCols() - 1) System.out.print(v.get(i).get(j));
                else System.out.print(v.get(i).get(j) + " ");
            }
            System.out.println("]");
        }
    }

    public ArrayList<Double> multRow(double x, int i) {
        ArrayList<Double> u = new ArrayList<>(v.get(i));
        u.replaceAll(aDouble -> x * aDouble);
        return u;
    }

    public Matrix getUpperTriangle() {
        Matrix matrix = new Matrix(this);
        for (int i = 0; i < matrix.getCols(); i++) {
            int pvtInd;
            try {
                pvtInd = matrix.getPvt(i);
            } catch (Exception e) {
                continue;
            }
            matrix.rowsSwap(pvtInd, i);
            for (int j = i + 1; j < matrix.getRows(); j++) {
                double k = (-1) * matrix.getE(j, i) / matrix.getE(i, i);
                ArrayList<Double> x = matrix.multRow(k, i);
                matrix.rowsAdd(j, x);
            }

        }
        return matrix;
    }

    public Matrix concat(ArrayList<Double> b) {
        Matrix matrix = new Matrix(this);

        for (int i = 0; i < getRows(); i++) {
            matrix.v.get(i).add(b.get(i));
        }
        if(getRows()==0){
            for(int i=0;i<b.size();i++){
                matrix.v.add(new ArrayList<>());
                matrix.v.get(i).add(b.get(i));
            }
        }
        return matrix;
    }

    public ArrayList<Double> solution(ArrayList<Double> b) {
        Matrix matrix = new Matrix(this).concat(b).getUpperTriangle();
        ArrayList<Double> roots = new ArrayList<>();

        for (int i = getRows() - 1; i >= 0; i--) {
            double x = matrix.getE(i, getCols());
            for (int j = 0; j < roots.size(); j++) {
                x -= matrix.getE(i, i + j + 1) * roots.get(roots.size() - j - 1);
            }
            if (Math.abs(matrix.getE(i, i)) < Math.pow(10, -9)) {
                System.out.println("det(M)==0");
                return new ArrayList<>();
            } else x /= matrix.getE(i, i);
            roots.add(x);
        }
        Collections.reverse(roots);
        return roots;
    }

    private Matrix createC() {
        Matrix matrixC = new Matrix(getRows(), getCols());
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                if (i != j) matrixC.setE(i, j, (-1) * getE(i, j) / getE(i, i));
            }
        }
        return matrixC;
    }

    private ArrayList<Double> createD(ArrayList<Double> b) {
        ArrayList<Double> d = new ArrayList<>();
        for (int i = 0; i < b.size(); i++) {
            d.add(b.get(i) / getE(i, i));
        }
        return d;
    }

    public static boolean cheackAccuracy(double acc, ArrayList<Double> y, ArrayList<Double> x) {

        double sum = 0;
        for (int i = 0; i < y.size(); i++) {
             if(Math.abs(x.get(i) - y.get(i))>=acc)return false;
        }
        return true;
    }

    public ArrayList<Double> zeidelSolution(ArrayList<Double> b) {
        ArrayList<Double> x = new ArrayList<>();
        for (int i = 0; i < getRows(); i++) x.add(1.0);
        Matrix c = createC();
        ArrayList<Double> d = createD(b);
        double acc = Math.pow(10, -10);
        while (true) {
            ArrayList<Double> newX = new ArrayList<>();
            ArrayList<Double> cx = c.multVec(x);
            for (int i = 0; i < d.size(); i++) {
                newX.add(cx.get(i) + d.get(i));
            }
            if(cheackAccuracy(acc, newX, x)) {
                break;
            }
            x = newX;
        }
        return x;
    }

}
