package com.model2.mvc.web.product;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

@Controller
public class ProductController {
		
		
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	public ProductController() {
		System.out.println(this.getClass());
	}
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/addProductView.do")
	public String addProductView() throws Exception {
		System.out.println("/addProductView.do");
		
		return "redierct:/user/addProductView.jsp";
	}
	@RequestMapping("/addProduct.do")
	public String addProduct(@ModelAttribute("product") Product product) throws Exception {
		
		System.out.println("/addProduct.do");
		
		productService.addProduct(product);
		
		return "forward:/product/getProduct.jsp";
	}
	@RequestMapping("/getProduct.do")
	public String getProduct(@RequestParam("prodNo") int prodNo, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("/getProduct.do");
		
		Product product = productService.getProduct(prodNo);
		
		model.addAttribute("product",product);
		
		boolean added = false;
		String history = null;
		Cookie[] cookies = request.getCookies();
	
		if (cookies!=null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals("history")) {
					history = cookie.getValue();
					history+=","+prodNo;
					cookie.setValue(history);
					response.addCookie(cookie);
					added=true;
				}
			}
			if (!added) {
				Cookie cookie = new Cookie("history",prodNo+"");
				response.addCookie(cookie);
			}
		}
		return "forward:/product/getProduct.jsp";
	}
	@RequestMapping("/updateProductView.do")
	public String updateProductView( @RequestParam("prodNo") int prodNo, Model model) throws Exception {
		
		System.out.println("/updateProductView.do");
		
		Product product = productService.getProduct(prodNo);
		
		model.addAttribute("product",product);
		
		return "forward:/product/updateProduct.jsp";
	}
	@RequestMapping("/updateProduct.do")
	public String updateProduct(@ModelAttribute("product") Product product, Model model, HttpServletRequest request ) throws Exception{
		
		System.out.println("/updateProduct.do");		
		productService.updateProduct(product);
		
		
		return  "redirect:/getProduct.do?prodNo="+product.getProdNo()+"&menu="+request.getParameter("menu");
	}
	

	@RequestMapping("/listProduct.do")
	public String listProduct(@ModelAttribute("search") Search search, Model model, HttpServletRequest request) throws Exception {
		
		System.out.println("/listProduct.do");
		
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		

		
Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model °ú View ¿¬°á
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/product/listProduct.jsp";
	}
}
