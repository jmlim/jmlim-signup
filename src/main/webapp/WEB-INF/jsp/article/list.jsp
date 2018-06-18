<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="ko-KR">
<head>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body>
	글쓰기 테스트<br/>
	<br> time: ${time}
	<a href="/article/create">글쓰기</a>
	<h4>Default</h4>
	<div id="table-wrapper" class="table-wrapper">
		<table>
			<thead>
				<tr>
					<th>No</th>
					<th>Email</th>
					<th>Title</th>
					<th>Date</th>
				</tr>
			</thead>
			<tbody id="table-list">
			</tbody>
		</table>
		
		<h4>Pagination</h4>
		<ul class="pagination">
		<!-- <span class="button prev-btn disabled">Prev</span> -->
			<li id="li-prev-btn"><a href="#" class="button prev-btn disabled">Prev</a></li>
			<li id="li-next-btn"><a href="#" class="button next-btn">Next</a></li>
		</ul>
	</div>
	
	<%@ include file="/WEB-INF/jspf/footer.jspf"%>
	<!-- template tag 영역 -->
	<template id="table-template">
		<tr class="table-row">
			<td class="table-list-no">1</td>
			<td class="table-list-name">jmlim@naver.com</td>
			<td class="table-list-title"><a href="#">뀨뀨까까</a></td>
			<td class="table-list-date">2018-06-18 13:34:11</td>
		</tr>
	</template>

	<template id="page-list-template">
		<li class="page-li"><a href="#" class="page">1</a></li>
	</template>

	<script type="text/javascript">
	$(document).ready(function() {
		/**
			ajax 요청
		*/
		var url = "/article/article";
		coAjax({
			 url: url + "?page=0&size=10&sort=id,desc",
			 success: listRenderer
		});
		//Pageable을 사용하면 간단하다. 이 컨트롤러 메소드를 호출하는 쪽에서 page, size, sort등을 파라미터로 던지기만 하면 된다.
		//ex) localhost:8080/emp/list?page=0&size=10&sort=ename,desc
		//내용 : http://ojc.asia/bbs/board.php?bo_table=LecJpa&wr_id=281
		$( document ).on( "click", "#table-wrapper .page", function( e ) {
			var pageText = $(e.target).text();
			coAjax({
				 url:  url + "?page=" + ( Number(pageText) - 1) + "&size=10&sort=id,desc",
				 success: listRenderer
			});
		});

		//이전
		$( document ).on( "click", "#table-wrapper .prev-btn", function( e ) {
			if($(e.target).hasClass("disabled")) {
				console.log("disabled");
				return;
			}
			var pageText = $(".pagination .active").text();
			var currentPage = Number(pageText) - 1;
			var pageNum = (Math.floor( currentPage / 10) - 1) * 10;
			coAjax({
				 url:  url + "?page=" + pageNum + "&size=10&sort=id,desc",
				 success: listRenderer
			});
		});

		//다음
		$( document ).on( "click", "#table-wrapper .next-btn", function( e ) {
			if($(e.target).hasClass("disabled")) {
				console.log("disabled");
				return;
			}
			var pageText = $(".pagination .active").text();
			var currentPage = Number(pageText) - 1;
			var pageNum = (Math.floor( currentPage / 10) + 1) * 10;
			console.log(pageNum)
			coAjax({
				 url:  url + "?page=" + pageNum + "&size=10&sort=id,desc",
				 success: listRenderer
			});
		});
		

		/**
			리스트 렌더링
		*/
		function listRenderer(data) {
			console.log(data);
			var $tableList = $("#table-list");
			$tableList.empty();
			var temp = $("#table-template")[0];
			var clon = temp.content.cloneNode(true);
			var table = clon.querySelector(".table-row");
			
			//list
			if(data && data.totalElements > 0) {
				console.log(data);
				$.each(data.content, function(ind, obj) {	
					console.log('obj'+ind, obj);
					var $table = $(table);
					$table.find(".table-list-no").text(obj.id)
					$table.find(".table-list-name").text(obj.writer.email);
					$table.find(".table-list-title a")
							.attr("href","/article/content/" + obj.id).text(obj.title)
					$table.find(".table-list-date").text(obj.updatedDate);
					$tableList.append($table.clone());
				});
			}
			//page nav
			pageRenderer(data);
		}
		
		function pageRenderer(data) {
			var $pagination = $(".pagination");
			$pagination.find(".page-li").remove();
			var temp = $("#page-list-template")[0];
			var clon = temp.content.cloneNode(true);
			var li = clon.querySelector("li");
			//page
			if(data && data.totalPages > 0) {
				$pagination.find(".prev-btn").removeClass("disabled");
				$pagination.find(".next-btn").removeClass("disabled");
				//현재페이지
				var currentPage = data.number;
				var startInd = Math.floor(currentPage / 10) * 10;
				var endInd = startInd + 10;
			
				endInd = endInd > data.totalPages ? data.totalPages : endInd;
				console.log(endInd, data.totalPages)
				if(startInd == 0) {
					$pagination.find(".prev-btn").addClass("disabled");
				}
				if(endInd === data.totalPages) {
					$pagination.find(".next-btn").addClass("disabled");
				}

				for(var i = startInd, len = endInd; i < len; i++) {
					//<li><a href="#" class="page">2</a></li>
					var $li = $(li);
					var $a = $li.find("a");
					$a.text(i+1);
					if(currentPage == i) {
						$a.addClass("active");
					} else {
						$a.removeClass("active");
					}
					$pagination.find("#li-next-btn").before($li.clone());
					//$pagination.append($li.clone());
				}
			}
		}
	});

	</script>
</body>
</html>
