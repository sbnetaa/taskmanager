package ru.terentyev.TaskManager.exceptions;

public class IncompatibleCriteriaException extends RuntimeException {

	private long objectId;

	public IncompatibleCriteriaException(long objectId) {
		super("При GET запросе нельзя одновременно "
				+ "указывать ID с другими критериями для поиска. Была выбрана запись с указанным ID.");
		this.objectId = objectId;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}



}
