public class BoundingBox {
    double xmin = Double.NaN;
    double ymin = Double.NaN;
    double xmax = Double.NaN;
    double ymax = Double.NaN;

    public String toString(){
        return String.format("%f %f %f %f", xmin, xmax, ymin, ymax);
    }


    /**
     * Powi�ksza BB tak, aby zawiera� punkt (x,y)
     * Je�eli by� wcze�niej pusty - w�wczas ma zawiera� wy��cznie ten punkt
     * @param x - wsp�rz�dna x
     * @param y - wsp�rz�dna y
     */
    void addPoint(double x, double y){
        if(isEmpty()){
            xmin = x;
            xmax = x;
            ymin = ymax = y;
        }
        else{
            xmax = Math.max(x, xmax);
            xmin = Math.min(x, xmin);
            ymax = Math.max(y, ymax);
            ymin = Math.min(y, ymin);
        }
    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y){
        return x>=xmin && x<=xmax && y>=ymin && y<=ymax;
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb){
        return this.contains(bb.xmax, bb.ymax) && this.contains(bb.xmin, bb.ymin) &&
                this.contains(bb.xmax, bb.ymin) && this.contains(bb.xmin, bb.ymax);
    }

    /**
     * Sprawdza, czy dany BB przecina si� z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb){
        return this.contains(bb.xmax, bb.ymax) || this.contains(bb.xmin, bb.ymin) ||
            this.contains(bb.xmax, bb.ymin) || this.contains(bb.xmin, bb.ymax) || bb.contains(this.xmax, this.ymax) ||
                bb.contains(this.xmin, this.ymin) || bb.contains(this.xmax, this.ymin) || bb.contains(this.xmin, this.ymax);
    }
    /**
     * Powi�ksza rozmiary tak, aby zawiera� bb oraz poprzedni� wersj� this
     * Je�eli by� pusty - po wykonaniu operacji ma by� r�wny bb
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb){
        if(isEmpty()){
            xmin = bb.xmin;
            xmax = bb.xmax;
            ymin = bb.ymin;
            ymax = bb.ymax;
        }
        else{
            xmax = Math.max(bb.xmax, xmax);
            xmin = Math.min(bb.xmin, xmin);
            ymax = Math.max(bb.ymax, ymax);
            ymin = Math.min(bb.ymin, ymin);
        }
        return this;
    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */
    boolean isEmpty(){
        return Double.isNaN(xmin);
    }

    /**
     * Sprawdza czy
     * 1) typem o jest BoundingBox
     * 2) this jest r�wny bb
     * @return
     */
    public boolean equals(Object o){
        return o instanceof BoundingBox && ((BoundingBox) o).xmax == xmax && ((BoundingBox) o).xmin == xmin &&
                ((BoundingBox) o).ymax == ymax && ((BoundingBox) o).ymin == ymin;
    }

    /**
     * Oblicza i zwraca wsp�rz�dn� x �rodka
     * @return if !isEmpty() wsp�rz�dna x �rodka else wyrzuca wyj�tek
     * (sam dobierz typ)
     */
    double getCenterX(){
        if(isEmpty()){
            throw new RuntimeException("Can't evaluate Xcenter of an empty square");}
        return (xmax + xmin) / 2;
    }
    /**
     * Oblicza i zwraca wsp�rz�dn� y �rodka
     * @return if !isEmpty() wsp�rz�dna y �rodka else wyrzuca wyj�tek
     * (sam dobierz typ)
     */
    double getCenterY(){
        if(isEmpty()){
            throw new RuntimeException("Can't evaluate Ycenter of an empty square");}
        return (ymax + ymin) / 2;
    }

    /**
     * Oblicza odleg�o�� pomi�dzy �rodkami this bounding box oraz bbx
     * @param bbx prostok�t, do kt�rego liczona jest odleg�o��
     * @return if !isEmpty odleg�o��, else wyrzuca wyj�tek lub zwraca maksymaln� mo�liw� warto�� double
     * Ze wzgl�du na to, �e s� to wsp�rz�dne geograficzne, zamiast odleg�o�ci u�yj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod mo�na znale�� w Internecie...
     */
    double distanceTo(BoundingBox bbx){
        double x1 = this.getCenterX();
        double y1 = this.getCenterY();
        double x2 = bbx.getCenterX();
        double y2 = bbx.getCenterY();
        y1 = Math.toRadians(y1);
        x1 = Math.toRadians(x1);
        y2 = Math.toRadians(y2);
        x2 = Math.toRadians(x2);

        if(this.isEmpty()){
            return Double.MAX_VALUE;
        }
        else{
            return 2 * 6371* Math.asin(Math.sqrt(Math.pow(Math.sin((y2-y1)/2), 2) +
                    Math.cos(y1) * Math.cos(y2) * Math.pow(Math.sin((x2-x1)/2), 2)));
        }
    }

}