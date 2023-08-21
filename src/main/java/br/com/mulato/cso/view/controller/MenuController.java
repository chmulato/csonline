package br.com.mulato.cso.view.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import br.com.mulato.cso.dry.AbstractController;

public class MenuController  extends AbstractController implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private MenuModel model;
 
    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
 
        //First submenu
        DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Submenu")
                .build();
 
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("External")
                .url("http://www.primefaces.org")
                .icon("pi pi-home")
                .build();
        firstSubmenu.getElements().add(item);
 
        model.getElements().add(firstSubmenu);
 
        //Second submenu
        DefaultSubMenu secondSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Actions")
                .build();
 
        item = DefaultMenuItem.builder()
                .value("Save")
                .icon("pi pi-save")
                .command("#{menuView.save}")
                .update("messages")
                .build();
        secondSubmenu.getElements().add(item);
 
        item = DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-times")
                .command("#{menuView.delete}")
                .ajax(false)
                .build();
        secondSubmenu.getElements().add(item);
 
        item = DefaultMenuItem.builder()
                .value("Redirect")
                .icon("pi pi-search")
                .command("#{menuView.redirect}")
                .build();
        secondSubmenu.getElements().add(item);
 
        model.getElements().add(secondSubmenu);
    }
 
    public MenuModel getModel() {
        return model;
    }
 
    public void save() {
        addMessage("Success", "Data saved");
    }
 
    public void update() {
        addMessage("Success", "Data updated");
    }
 
    public void delete() {
        addMessage("Success", "Data deleted");
    }
 
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public String changePassword() {
		return goToPage("change_password");
	}
    
}