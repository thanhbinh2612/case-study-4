package com.codegym.controller;

import com.codegym.model.Item;
import com.codegym.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/favoritesList")
public class FavoritesListController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private BillDetailService billDetailService;

    @Autowired
    private BillService billService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SendEmailService sendEmailService;

    @GetMapping("")
    public ModelAndView displayFavoritesList(HttpSession session){
        if (session.getAttribute("list") == null){
            List<Item> list = new ArrayList<Item>();
            session.setAttribute("list", list);
        }
        if(session.getAttribute("cart") == null){
            List<Item> cart = new ArrayList<Item>();
            session.setAttribute("cart", cart);
        }
        ModelAndView modelAndView = new ModelAndView("main/favoritesList");
        modelAndView.addObject("totalFavorite", sumInFavoritesList(session));
        modelAndView.addObject("total",sum(session));
        return modelAndView;
    }

//    @GetMapping("/displayTotalInCart")
//    public ModelAndView displayTotalInCart(HttpSession session){
//        if (session.getAttribute("cart") == null){
//            List<Item> cart = new ArrayList<Item>();
//            session.setAttribute("cart", cart);
//        }
//        ModelAndView modelAndView = new ModelAndView("main/favoritesList");
//        modelAndView.addObject("totalWhenOperatedToCart", sumInFavoritesList(session));
//        return modelAndView;
//    }

    @RequestMapping(value = "/display/{id}", method = RequestMethod.GET)
    public String displayProductInFavoritesList(@PathVariable("id") String id, HttpSession session){
        if (session.getAttribute("list") == null){
            List<Item> list = new ArrayList<Item>();
            list.add(new Item(productService.findById(id), 1));
            session.setAttribute("list", list);
        } else {
            List<Item> list = (List<Item>) session.getAttribute("list");
            int index = isExist(id, list);
            System.out.println("index: " + index);
            if (index == -1){
                list.add(new Item(productService.findById(id),1));
            } else {
                int quantity = list.get(index).getQuantity() +1;
                list.get(index).setQuantity(quantity);
            }
            session.setAttribute("list", list);
        }
        return "redirect:/favoritesList";
    }

    @RequestMapping(value = "/operate/{id}", method = RequestMethod.GET)
        public String fromFavoritesToCart(@PathVariable("id") String id, HttpSession session){
            if(session.getAttribute("cart") == null){
                List<Item> newCart = new ArrayList<Item>();
                session.setAttribute("cart", newCart);
            }
            List<Item> cart = (List<Item>) session.getAttribute("cart");
            List<Item> wishlist = (List<Item>) session.getAttribute("list");
            int index = isExist(id, wishlist);
            Item selectedItem = wishlist.get(index);
            int index1 = isExist(id, cart);
            if (index1 < 0) {
                cart.add(selectedItem);
            } else {
                Item item = cart.get(index1);
                item.setQuantity(item.getQuantity() + selectedItem.getQuantity());
            }
            wishlist.remove(index);
            session.setAttribute("cart",cart);
            session.setAttribute("list",wishlist);
            return "redirect:/favoritesList";



//            if (session.getAttribute("cart") == null){
//                List<Item> cart = new ArrayList<Item>();
//                cart.add(new Item(productService.findById(id), 1));
//                session.setAttribute("cart", cart);
//            } else {
//                List<Item> cart = (List<Item>) session.getAttribute("cart");
//                int index = isExist(id, cart);
//                System.out.println("index: " + index);
//                if (index == -1){
//                    cart.add(new Item(productService.findById(id),1));
//                } else {
//                    int quantity = cart.get(index).getQuantity() +1;
//                    cart.get(index).setQuantity(quantity);
//                }
//                session.setAttribute("cart", cart);
//            }
//        List<Item> list = (List<Item>) session.getAttribute("list");
//        int index1 = isExistInFavoritesList(id, list);
//        list.remove(index1);
//        session.setAttribute("list", list);
//        return "redirect:/favoritesList/displayTotalInCart";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpSession session) {
        String[] quantities = request.getParameterValues("quantity");
        List<Item> list = (List<Item>) session.getAttribute("list");
        for (int i = 0 ; i < list.size();i++){
            list.get(i).setQuantity(Integer.parseInt(quantities[i]));
        }
        session.setAttribute("list", list);
        return "redirect:/favoritesList";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String remove(@PathVariable("id") String id, HttpSession session) {
        List<Item> list = (List<Item>) session.getAttribute("list");
        int index = isExist(id, list);
        list.remove(index);
        session.setAttribute("list", list);
        return "redirect:/favoritesList";
    }

    private int isExist(String id, List<Item> cart){
        for (int i=0; i <cart.size();i++){
            if (cart.get(i).getProduct().getId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    private double sumInFavoritesList(HttpSession session){
        List<Item> list = (List<Item>) session.getAttribute("list");
        double s = 0;
        for (Item itemFavoritesList: list){
            s += itemFavoritesList.getQuantity()
                    * itemFavoritesList.getProduct().getPrice();
        }
        return s;
    }

    private double sum(HttpSession session){
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        double s = 0;
        for (Item item: cart){
            s += item.getQuantity()
                    * item.getProduct().getPrice();
        }
        return s;
    }
}
