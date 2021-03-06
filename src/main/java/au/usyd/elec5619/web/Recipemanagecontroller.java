package au.usyd.elec5619.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import au.usyd.elec5619.domain.Category;
import au.usyd.elec5619.domain.Collection;
import au.usyd.elec5619.domain.Comment;
import au.usyd.elec5619.domain.Ingredient;
import au.usyd.elec5619.domain.Recipe;
import au.usyd.elec5619.domain.Step;
import au.usyd.elec5619.domain.User;
import au.usyd.elec5619.service.Commentcreater;
import au.usyd.elec5619.service.Recipecreater;
import au.usyd.elec5619.service.Usercreater;


@Controller
@RequestMapping(value="/recipe/**")
public class Recipemanagecontroller {
	
	@Autowired
	private Recipecreater recipecreater;
	@Autowired
	private Usercreater usercreater;
	@Autowired
	private Commentcreater commentcreater;
	//把category调出来 放下拉刘表
	@ModelAttribute("category")
	public List<Category> getallcategory(){
		List<Category> category = recipecreater.getallcategories();
		return category;
	}
	
	//加载recipe form
	@RequestMapping(value="/showcreaterecipeform", method=RequestMethod.GET)
	public String showcreaterecipeform() {
		return "newrecipecreater";
	}
	//添加完整菜谱
	@RequestMapping(value="/addrecipetotal", method=RequestMethod.POST)
	public String addrecipetotal(HttpServletRequest request,HttpServletResponse response, String[] ingredientName, String[] ingredientAmount, String[] description, @RequestParam("dish_img") MultipartFile dishfile,@RequestParam("steppicture") MultipartFile[] file)  throws Exception, IOException {
			//生成ingredient list
			List<Ingredient> ingredientlist = new ArrayList<Ingredient>();
			for (int i = 0; i <ingredientName.length; i++) {
				Ingredient ingredient = new Ingredient();
				ingredient.setIngredientName(ingredientName[i]);
				ingredient.setIngredientAmount(ingredientAmount[i]);
				ingredientlist.add(ingredient);
			}
			//将图片存储到指定文件夹中
			String serverpath = request.getSession().getServletContext().getRealPath("img");
			String dishpath = recipecreater.uploadpicture(dishfile,serverpath);
			//创建step list
			List<Step> steplist = new ArrayList<Step>();
			int stepid=1;
			for (int i = 0; i < description.length; i++ ) {		
	            Step step = new Step(); 
	            step.setstepsno(stepid);
	            step.setdescription(description[i]);
	            String imgpath;
	            if(file[i].getOriginalFilename()=="") {
	            	imgpath="";
	            }
	            else {
	            	imgpath = recipecreater.uploadpicture(file[i], serverpath);
	            }	           
            step.setstepImg(imgpath);
	            steplist.add(step);
	            stepid++;
	        }
			//用session获取用户id
			HttpSession session = request.getSession(true);
			int userid = (Integer) session.getAttribute("userid");
			Recipe recipe1 = new Recipe(request.getParameter("recipeName"),Integer.parseInt(request.getParameter("cookTime")),Integer.parseInt(request.getParameter("servepeopleno")),dishpath ,request.getParameter("tips"),Integer.parseInt(request.getParameter("categoryID")), userid ,ingredientlist,steplist);
			recipecreater.addrecipe(recipe1);
			usercreater.trade(20, userid, 0);
			return "redirect:/";	
	}
	
	//加载更新页面 需要recipeid
	@RequestMapping(value="/updatepage/{recipeID}/{userid}", method=RequestMethod.GET)
	public ModelAndView updatepage(@PathVariable("recipeID") int recipeID,@PathVariable("userid") int userid, HttpServletRequest request,HttpServletResponse response) {
		Recipe recipe = recipecreater.getrecipebyID(recipeID);
		List<Ingredient> ingredientlist = recipe.getIngredientlist();
		List<Step> steplist = recipe.getSteplist();
		User user = usercreater.getUserById(userid);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipe);
		myModel.put("ingredients", ingredientlist);
		myModel.put("steps", steplist);
		myModel.put("categoryID", recipe.getcategoryID());
		myModel.put("user", user);
		return new ModelAndView("updaterecipe","model",myModel);
	}
	//更新菜谱
	@RequestMapping(value="/updaterecipe", method=RequestMethod.POST)
	public String updaterecipe(HttpServletRequest request,HttpServletResponse response, Integer[] ingredientID, String[] ingredientName, String[] ingredientAmount, Integer[] stepsID, Integer[] stepid, String[] description, String[] originstepImg, @RequestParam("dish_img") MultipartFile dishfile,@RequestParam("steppicture") MultipartFile[] file) throws Exception, IOException{
		List<Ingredient> ingredientlist = new ArrayList<Ingredient>();
		for (int i = 0; i <ingredientName.length; i++) {
			Ingredient ingredient = new Ingredient();
			if(ingredientID[i]!=0) {
				ingredient.setIngredientID(ingredientID[i]);
			}
			ingredient.setIngredientName(ingredientName[i]);
			ingredient.setIngredientAmount(ingredientAmount[i]);
			ingredientlist.add(ingredient);
		}
		//判断是否需要更新照片
		String serverpath = request.getSession().getServletContext().getRealPath("img");
		String dishpath;
		String filename = dishfile.getOriginalFilename();
		System.out.println(filename);
		if(dishfile.getOriginalFilename()!="") {
			dishpath = recipecreater.uploadpicture(dishfile,serverpath);
		}
		else {
			dishpath = request.getParameter("origindishImg");
		}
		List<Step> steplist = new ArrayList<Step>();
		for (int i = 0; i < stepid.length; i++ ) {		
            Step step = new Step();
            if(stepsID[i]!=0) {
            	step.setstepsID(stepsID[i]);
            }
            step.setstepsno(stepid[i]);
            step.setdescription(description[i]);
            if(file[i].getOriginalFilename()!="") {
            	String imgpath = recipecreater.uploadpicture(file[i], serverpath);
            	step.setstepImg(imgpath);
            }
            else {
            	String imgpath = originstepImg[i];
            	step.setstepImg(imgpath);
            }
            steplist.add(step);
        }
		int userID = Integer.parseInt(request.getParameter("userid"));
		Recipe recipe = new Recipe(request.getParameter("recipeName"),Integer.parseInt(request.getParameter("cookTime")),Integer.parseInt(request.getParameter("servepeopleno")),dishpath ,request.getParameter("tips"),Integer.parseInt(request.getParameter("categoryID")), userID ,ingredientlist,steplist);
		recipe.setrecipeID(Integer.parseInt(request.getParameter("recipeID")));
		recipecreater.updaterecipe(recipe);
		return "redirect:/recipe/userrecipe/"+userID;
	}
	
	//删除菜谱
	@RequestMapping(value="/deleterecipe/{recipeID}", method=RequestMethod.GET)
	public String deleterecipe(@PathVariable("recipeID") int recipeID, HttpServletRequest request,HttpServletResponse response) {
		//String recipeID = request.getParameter("recipeID");
		//System.out.println("cccc"+recipeID);
		recipecreater.deleterecipe(recipeID);
		return "redirect:/recipe/userrecipe/1";
	}
	
	//显示所有菜谱
	@RequestMapping(value="/allrecipes", method=RequestMethod.GET)
	public ModelAndView getallrecipes(HttpServletRequest request,HttpServletResponse response) {
		List<Recipe> recipelist = recipecreater.getallrecipes();
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipelist);
		myModel.put("categoryid", 0);
		myModel.put("cooktime", 0);
		return new ModelAndView("showrecipes","model",myModel);
		
	}
	
	//显示菜谱详情
	@RequestMapping(value="/recipedetails/{id}", method=RequestMethod.GET)
	public ModelAndView showrecipedetails(@PathVariable("id") int id,HttpServletRequest request,HttpServletResponse response) {
		Recipe recipe = recipecreater.getrecipebyID(id);
		String categoryName = recipecreater.getcategoryname(recipe.getcategoryID());
		List<Ingredient> ingredientlist = recipe.getIngredientlist();
		List<Step> steplist = recipe.getSteplist();
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipe);
		myModel.put("ingredients", ingredientlist);
		myModel.put("steps", steplist);
		myModel.put("categoryName", categoryName);
		HttpSession session = request.getSession(true);
		myModel.put("collect", 0);
		if(session.getAttribute("username")!="") {
			String username = (String)session.getAttribute("username");
			int userid = (Integer) session.getAttribute("userid");
			System.out.println(username);
			myModel.put("username", username);
			myModel.put("userid", userid);
			boolean ifcollected = recipecreater.checkcollection(userid, id);
			System.out.println(ifcollected);
			if(ifcollected) {
				myModel.put("collect", 1);
			}
			else {
				myModel.put("collect", 0);
			}
		}
		
		List<Comment> commentlist = commentcreater.getcommentbyID(id);
		myModel.put("comments", commentlist);
		return new ModelAndView("recipedetail","model",myModel);
	}
	
	//按类别查菜谱
	@RequestMapping(value="/categoryrecipe/{categoryID}", method=RequestMethod.GET)
	public ModelAndView getrecipebycategory(@PathVariable("categoryID") int categoryID) {
		//String categoryID = request.getParameter("categoryID");
		System.out.println(categoryID);
		//String categoryID = "4";
		List<Recipe> recipelist = recipecreater.getrecipebycategory(categoryID);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipelist);
		myModel.put("categoryid", categoryID);
		myModel.put("cooktime", 0);
		return new ModelAndView("showrecipes","model",myModel);
	}
	//查询用户发布信息
	@RequestMapping(value="/userrecipe/{userid}", method=RequestMethod.GET)
	public ModelAndView getrecipebyuser(@PathVariable("userid") int userid,HttpServletRequest request,HttpServletResponse response) {
		User user = usercreater.getUserById(userid);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("user", user);
		List<Recipe> recipelist = recipecreater.getrecipebyuser(userid);
		myModel.put("recipes", recipelist);
		return new ModelAndView("showrecipesbyuser","model",myModel);
	}
	//按时间查菜谱
	@RequestMapping(value="/cooktimerecipe/{cooktime}", method=RequestMethod.GET)
	public ModelAndView getrecipebycooktime(@PathVariable("cooktime") int cooktime) {
		List<Recipe> recipelist = recipecreater.getrecipebycooktime(cooktime);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipelist);
		myModel.put("cooktime", cooktime);
		myModel.put("categoryid", 0);
		return new ModelAndView("showrecipes","model",myModel);
	}
	//按时间类别查询
	@RequestMapping(value="/cooktimerecipe/{cookTime}/{categoryID}", method=RequestMethod.GET)
	public ModelAndView getrecipebytimeandrecipe(@PathVariable("cookTime") int cookTime,@PathVariable("categoryID") int categoryID) {
		List<Recipe> recipelist = recipecreater.getrecipebytimeandtype(categoryID, cookTime);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipelist);
		myModel.put("categoryid", categoryID);
		myModel.put("cooktime", cookTime);
		return new ModelAndView("showrecipes","model",myModel);
	}
	//API显示所有类别
	@RequestMapping(value="/categoriestest", method=RequestMethod.GET)
	@ResponseBody
	public List<Category> getallcategoryapitest() {
		List<Category> categorylist = recipecreater.getallcategories();
		return categorylist;
	}
	@RequestMapping(value="/showcategory", method=RequestMethod.GET)
	public String showcategory() {
		return "admincategory";
	}
	//收藏菜谱
	@RequestMapping(value="/collectrecipe", method=RequestMethod.POST)
	@ResponseBody
	public String collectrecipe(HttpServletRequest request,HttpServletResponse response) {
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));
		int userid = Integer.parseInt(request.getParameter("userid"));
		Collection collection = new Collection(recipeID,userid);
		recipecreater.addcollection(collection);
		return "home";
	}
	//显示收藏菜谱
	@RequestMapping(value="/mycollection/{userid}", method=RequestMethod.GET)
	public ModelAndView getrecipebycollection(@PathVariable("userid") int userid) {
		List<Recipe> recipelist = recipecreater.getrecipecollectbyuser(userid);
		User user = usercreater.getUserById(userid);
		Map<String, Object> myModel = new HashMap<String, Object>();
		myModel.put("recipes", recipelist);
		myModel.put("user", user);
		return new ModelAndView("usercollection","model",myModel);
	}
	//取消收藏
	@RequestMapping(value="/deletecollect/{userid}/{recipeID}", method=RequestMethod.GET)
	public String deletecollection(@PathVariable("userid") int userid,@PathVariable("recipeID") int recipeID) {
		recipecreater.deletecollection(userid, recipeID);
		return "redirect:/recipe/mycollection/"+userid;
	}
	//取消类别
	@RequestMapping(value="/categorydelete/{categoryID}", method=RequestMethod.GET)
	public String deletecategory(@PathVariable("categoryID") int categoryID, HttpServletRequest request,HttpServletResponse response) {
		recipecreater.deletecategory(categoryID);
		return "redirect:/recipe/showcategory";
	}
	//添加类别
	@RequestMapping(value="/addcategory", method=RequestMethod.POST)
	public String deletecategory(HttpServletRequest request,HttpServletResponse response) {
		String categoryname = request.getParameter("categoryname");
		Category category = new Category();
		category.setcategoryName(categoryname);
		recipecreater.addcategory(category);
		return "redirect:/recipe/showcategory";
	}
}
