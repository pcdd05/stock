package dna.rest.common;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Rest data wrapper class.
 *
 * @param <T> the generic type
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestResource<T> extends RestObject {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4665341237057768028L;

  /** The items. */
  @XmlAnyElement(lax = true)
  private List<T> items;

  /** The total items. */
  private Integer totalItems;

  /** The start index. */
  private Integer startIndex; // 1 based

  /** The items per page. */
  private Integer itemsPerPage; // maxResults

  /** The current item count. */
  private Integer currentItemCount; // size of items


  /**
   * Gets the items.
   * 
   * @return the items
   */
  public List<T> getItems() {
    return items;
  }

  /**
   * Sets the items.
   * 
   * @param items the new items
   */
  public void setItems(List<T> items) {
    this.items = items;
  }

  /**
   * Gets the total items.
   * 
   * @return the total items
   */
  public Integer getTotalItems() {
    return totalItems;
  }

  /**
   * Sets the total items.
   * 
   * @param totalItems the new total items
   */
  public void setTotalItems(Integer totalItems) {
    this.totalItems = totalItems;
  }

  /**
   * Gets the start index.
   * 
   * @return the start index
   */
  public Integer getStartIndex() {
    return startIndex;
  }

  /**
   * Sets the start index.
   * 
   * @param startIndex the new start index
   */
  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  /**
   * Gets the items per page.
   * 
   * @return the items per page
   */
  public Integer getItemsPerPage() {
    return itemsPerPage;
  }

  /**
   * Sets the items per page.
   * 
   * @param itemsPerPage the new items per page
   */
  public void setItemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  /**
   * Gets the current item count.
   * 
   * @return the current item count
   */
  public Integer getCurrentItemCount() {
    return currentItemCount;
  }

  /**
   * Sets the current item count.
   * 
   * @param currentItemCount the new current item count
   */
  public void setCurrentItemCount(Integer currentItemCount) {
    this.currentItemCount = currentItemCount;
  }


  // TODO override toString


}
