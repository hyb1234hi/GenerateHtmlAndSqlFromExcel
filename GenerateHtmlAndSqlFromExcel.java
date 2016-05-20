package com.ketayao.ketacustom.generate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sfm.csv.CsvParser;

import com.ketayao.ketacustom.generate.util.PinYinUtil;
import com.ketayao.ketacustom.generate.vo.Field;

public class GenerateHtmlAndSqlFromExcel {

	public static List<String[]> getContentFromFile(File file) throws IOException {
		List<String[]> content = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));

		Iterator<String[]> it = CsvParser.skip(0).iterator(br);
		while (it.hasNext()) {
			content.add(it.next());
		}

		br.close();

		return content;

	}

	public static List<Field> generateFields(String fileName) throws IOException {
		List<Field> fields = new ArrayList<Field>();

		File file = new File(fileName);

		List<String[]> details = getContentFromFile(file);

		String[] textList = details.get(0);
		String[] typeList = details.get(1);
		String[] multipleList = details.get(2);
		String[] optionalValueList = details.get(3);

		for (int i = 0; i < textList.length; i++) {

			Field field = new Field();

			if (i < textList.length) {

				field.setName(PinYinUtil.getFirstSpell(textList[i]));

			}
			if (i < textList.length) {
				field.setText(textList[i]);
			}
			if (i < typeList.length) {
				field.setPageControlType(typeList[i]);
			}
			if (i < multipleList.length) {
				field.setMultiple(multipleList[i]);
			}

			if (i < optionalValueList.length) {
				field.setOptionalValue(optionalValueList[i]);
			}

			fields.add(field);

		}

		return fields;
	}

	public static String getLabel(String text) {

		return "\r\n<label> " + text + " </label> ";

	}

	public static String getInputText(Field field) {

		StringBuffer sb = new StringBuffer();

		sb.append("<p> ");

		sb.append(getLabel(field.getText()) + "\r\n");

		sb.append("<input   type=\"text\" ");
		sb.append(" name = \"" + field.getName() + "\"");

		sb.append("  /> \r\n");

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getInputRadio(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p> ");

		sb.append(getLabel(field.getText()) + "\r\n");

		String[] values = field.getOptionalValue().split("#");

		for (String value : values) {

			sb.append("<input   type=\"radio\" ");
			sb.append(" name = \"" + field.getName() + "\"");

			sb.append("  />  " + value + "\r\n");

		}

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getInputHidden(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p> ");

		sb.append(getLabel(field.getText()) + "\r\n");

		sb.append("<input   type=\"hidden\" ");
		sb.append(" name = \"" + field.getName() + "\"");

		sb.append("  /> \r\n");

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getInputCheckbox(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p> ");

		sb.append(getLabel(field.getText()) + "\r\n");

		String[] values = field.getOptionalValue().split("#");

		for (String value : values) {

			sb.append("<input   type=\"checkbox\" ");
			sb.append(" name = \"" + field.getName() + "\"");

			sb.append("  />  " + value + "\r\n");

		}

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getSelect(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p> ");

		sb.append(getLabel(field.getText()) + "\r\n");

		sb.append("<select      ");

		sb.append(" name =\" " + field.getName() + "\"");

		if (field.getMultiple() != null) {
			sb.append(" multiple=\"multiple\"");
		}

		sb.append("  />  \r\n");

		String[] values = field.getOptionalValue().split("#");

		for (String value : values) {

			sb.append(" <option   type=\"checkbox\" ");
			sb.append(" name = \"" + field.getName() + "\"");

			sb.append("  > ");
			sb.append(value);

			sb.append(" </option> \r\n");

		}

		sb.append("</select> \r\n ");

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getTextarea(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p>  ");

		sb.append(getLabel(field.getText()) + "\r\n");

		sb.append(" <textarea     ");
		sb.append(" name = \"" + field.getName() + "\"");
		sb.append("  cols=\"80\" rows=\"10\" >  ");

		sb.append("</textarea> \r\n");

		sb.append("</p> \r\n");
		return sb.toString();
	}

	public static String getDatetime(Field field) {
		StringBuffer sb = new StringBuffer();

		sb.append("<p>  ");

		sb.append(getLabel(field.getText()) + "\r\n");

		sb.append("<input   type=\"text\" ");
		sb.append(" name = \"" + field.getName() + "\"");
		sb.append(" onClick=\"WdatePicker()\"/>  \r\n");

		sb.append("</p> \r\n");

		return sb.toString();
	}

	public static String getPageControlHtml(Field field) {

		String pageControlHtml = "";

		String type = field.getPageControlType() == null ? "" : field.getPageControlType();

		switch (type) {
		case "":
		case "text":
			pageControlHtml = getInputText(field);
			break;

		case "hidden":
			pageControlHtml = getInputHidden(field);
			break;

		case "radio":
			pageControlHtml = getInputRadio(field);
			break;
		case "checkbox":
			pageControlHtml = getInputCheckbox(field);
			break;
		case "select":
			pageControlHtml = getSelect(field);
			break;
		case "textarea":
			pageControlHtml = getTextarea(field);
			break;
		case "datetime":
			pageControlHtml = getDatetime(field);
			break;

		default:
			break;
		}

		return pageControlHtml;

	}

	public static String generatePageHtml(List<Field> fields) {
		StringBuffer sb = new StringBuffer();

		for (Field field : fields) {

			sb.append(getPageControlHtml(field));

		}

		return sb.toString();

	}

	public static String generateSql(String tableName, List<Field> fields, int type) throws IOException {

		StringBuffer sbSql = new StringBuffer();

		String sqlBegin = "create table ";

		sbSql.append(sqlBegin);

		sbSql.append(tableName);

		sbSql.append("\r\n(\r\n");

		for (Field field : fields) {

			sbSql.append("\t");

			sbSql.append(field.getName());

			String comment = "";

			if (type == 0) {
				comment = " comment  \' " + field.getText() + "\' ";
			}

			sbSql.append(" varchar(255) " + comment + "  ,\r\n");

		}

		sbSql.replace(sbSql.length() - 3, sbSql.length(), "");
		sbSql.append("\r\n)");

		sbSql.append("\r\n");

		if (type == 1) {
			for (Field field : fields) {

				sbSql.append("EXECUTE sp_addextendedproperty N'MS_Description', N'" + field.getText()
						+ "', N'user', N'dbo', N'table', N'" + tableName + "', N'column', N'" + field.getName()
						+ "'\r\n");

			}

		}

		return sbSql.toString();
	}

	public static void main(String[] args) throws IOException {

		List<Field> fields = generateFields("D:/1.csv");
		System.out.println(generatePageHtml(fields));
		System.out.println(generateSql("text", fields, 1));
		System.out.println("");

	}
}
