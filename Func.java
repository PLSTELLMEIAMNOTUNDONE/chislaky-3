import java.util.ArrayList;

public class Func {
    Calculated f;

    public Func(Calculated f) {
        this.f = f;
    }
    public double calc(double x){
        return f.calculate(x);
    }

    double calcProd(double x) {
        double acc = Math.pow(10, -6);
        double h = 0.005;

        double res = (calc(x + h) - calc(x)) / h;
        while (true) {
            h /= 2;

            double newRes = (calc(x + h) -calc(x)) / h;
            if (Math.abs(newRes - res) < acc) break;
            res = newRes;
        }
        return res;
    }
    public Func getProd() {
        Calculated c=(this::calcProd);
        return new Func(c);
    }

    public double root(double min, double max, double acc) {
        double x = min;
        double newX=x-(calc(x)/calcProd(x));
        while (true){
            newX=x-(calc(x)/calcProd(x));
            if(newX<min||newX>max){
                newX=(min+max)/2;
                double newBound=calc(newX);
                if(newBound>0)max=newX;
                if(newBound<=0)min=newX;
                return root(min,max,acc);
            }
            if(Math.abs(x-newX) < acc)break;
            x=newX;
        }
        return x;

    }

    public static void main(String[] args) {
        KvPolynom p = new KvPolynom();
        Func f = new Func(p);

        System.out.println(f.root( 1,2,Math.pow(10,-9)));


    }
}
