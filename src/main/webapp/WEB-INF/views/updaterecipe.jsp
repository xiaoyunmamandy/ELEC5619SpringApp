<%@ include file="/WEB-INF/views/include.jsp" %>
<html>
<head>
<style type="text/css">@import url("<c:url value='/resources/css/recipepage.css'/>");</style>
<style>
#dishimgbox{
text-align:center;
}
</style> 
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#categorieslist").val(${model.categoryID});
})

function addingredient(){
	var newrow = "<tr><td><input type='hidden' name='ingredientID' value='0'/><input type='text' name='ingredientName'></td><td><input type='text' name='ingredientAmount'></td></tr>";
	$(newrow).appendTo($('#ingredientsbox'))
}

function addsteps(){
	var rowcount = $('#stepbox').find('tr').length;
	stepno = rowcount +1;
	var newrow = "<tr><td><input type='hidden' name='stepsID' value='0'/><label>"+stepno+"</label><input type='hidden' name='stepid' value='"+stepno+"'/></td><td><input type='text' name='description'></td><td><input type='file' name='steppicture'></td>"
	$(newrow).appendTo($('#stepbox'))
	alert(rowcount)
}
</script>
</head>
<body>
<div class="topbar">111</div>
<form action="/elec5619Springapp/recipe/updaterecipe" method="post" enctype="multipart/form-data">
	<div class="row">
		<div class="col-xs-5">
			<div id="dishimgbox">
			<img src="/imgUrl/${model.recipes.dishImg }" height="150px" width="200px"/><br>
			change the dish picture:<input type="file" name="dish_img"/>
			<input type="hidden" name="origindishImg" value="${model.recipes.dishImg }"/>
			<input type="hidden" name="recipeID" value="${model.recipes.recipeID }" id="recipeid"/></div>
		</div>
		<div class="col-xs-7">
			<table>
				<tr>
					<td colspan="2">Recipe Name: <input type="text" name="recipeName" value="${model.recipes.recipeName}"/></td>
				</tr>
				<tr>
					<td>Cook time: <input type="text" name="cookTime" value="${model.recipes.cookTime}"/> mins</td>
					<td>Serve people number: <input type="text" name="servepeopleno" value="${model.recipes.servepeopleno}"/></td>
				</tr>
				<tr>
					<td>category: <form:select path="category" name="categoryID" id="categorieslist">
									<c:forEach items="${category}" var="category">
										<form:option value="${category.categoryID }" >  ${category.categoryName }</form:option>
									</c:forEach>
									</form:select></td>
					<td>Tips: <input type="text" name="tips" value="${model.recipes.tips}"/></td>
				</tr>
			</table>
		</div>
	</div>
	<table id="ingredientsbox">
		<c:forEach items="${model.ingredients }" var="ingredient">
			<tr>
				<td><input type="hidden" name="ingredientID" value="${ingredient.ingredientID }"/>
			<input type="text" name="ingredientName" value="${ingredient.ingredientName }"/></td>
				<td><input type="text" name="ingredientAmount" value="${ingredient.ingredientAmount }"/></td>
			</tr>
		</c:forEach>
	</table>
	<input type="button" value="add ingredients" onclick="addingredient();"/>
	<table id="stepbox">
		<c:forEach items="${model.steps }" var="step">
			<tr>
				<td><input type="hidden" name="stepsID" value="${step.stepsID }"/><span>${step.stepsno }</span><input type="hidden" name="stepid" value="${step.stepsno }"/></td>
				<td><input type="text" name="description" value="${step.description }"/></td>
				<td><img src="/imgUrl/${step.stepImg }" height="150px" width="200px"/><input type="hidden" value="${step.stepImg }" name="originstepImg"/></br><input type="file" name="steppicture"/></td>
			</tr>
		</c:forEach>
	</table>
	<input type="button" value="add steps" onclick="addsteps();"/>
	<input type="submit" value="update"/>
</form>
</body>
</html>