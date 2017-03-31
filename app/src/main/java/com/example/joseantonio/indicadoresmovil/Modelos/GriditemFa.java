package com.example.joseantonio.indicadoresmovil.Modelos;

/**
 * Created by luis  manuel cobian on 18/01/2017.
 */

public class GriditemFa extends GridItem {
    private String image;
    private String title;
    private String course;
    private String id;
    private String alerta;

    @Override
    public String getAlerta() {
        return alerta;
    }

    @Override
    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }




    public GriditemFa() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
