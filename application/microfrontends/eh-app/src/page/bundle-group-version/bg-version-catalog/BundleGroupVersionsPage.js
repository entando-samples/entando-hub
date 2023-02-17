import EhBreadcrumb from "../../../components/eh-breadcrumb/EhBreadcrumb";
import React, { useState, useEffect, useCallback } from "react";
import { getAllBundleGroupVersionByBundleGroupId, getAllCategories, getAllOrganisations } from "../../../integration/Integration";
import { Button, Content, Loading, Pagination, Search } from "carbon-components-react";
import { useParams } from "react-router-dom";
import CatalogTiles from "../../catalog/catalog-tiles/CatalogTiles";
import "./bundle-group-versions-page.scss";
import i18n from "../../../i18n";
import { Add16 } from '@carbon/icons-react'
import { useHistory } from "react-router-dom";
import CatalogFilterTile from "../../catalog/catalog-filter-tile/CatalogFilterTile";
import BundleGroupStatusFilter from "../../catalog/bundle-group-status-filter/BundleGroupStatusFilter";
import { INIT_PAGE, ITEMS_PER_PAGE } from "../../../helpers/constants";
import { useApiUrl } from "../../../contexts/ConfigContext";

let page_ = INIT_PAGE
let pageSizes = ITEMS_PER_PAGE
let currentPage = INIT_PAGE

const BundleGroupVersionsPage = ({ setVersionSearchTerm }) => {
  const [bgVersionList, setBgVersionList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([])
  const [totalItems, setTotalItems] = useState(12)
  const [bundleName, setBundleName] = useState("")
  const [statusFilterValue, setStatusFilterValue] = useState("")
  const [orgList, setOrgList] = useState([]);
  const history = useHistory()
  //signals the reloading need of the right side
  const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

  const apiUrl = useApiUrl();

  const { id: bundleGroupId, categoryId } = useParams();
  const IS_VERSIONS_PAGE = true;

  /*
  Callback to the Add and Edit (New Bundle Group) modal form submit
  This implementation ask for bundle groups tiles reloading
    */
  const onAfterSubmit = () => {
    setReloadToken(((new Date()).getTime()).toString()) //internal status change will rerender this component
  }

  const loadVersionData = (apiUrl, bundleGroupId, PAGE, ITEMS_PER_PAGE, statusFilterValue) => {
    return (getAllBundleGroupVersionByBundleGroupId(apiUrl, bundleGroupId, PAGE, ITEMS_PER_PAGE, statusFilterValue && statusFilterValue));
  }

  const onPaginationChange = async ({ page, pageSize }) => {
    pageSizes = pageSize
    page_ = page
    currentPage = page
    const response = await loadVersionData(apiUrl, bundleGroupId, page, pageSize, statusFilterValue);
    if (response && response.versions) {
      response.versions.payload && setBgVersionList(response.versions.payload)
      setTotalItems(response.versions.metadata.totalItems)
    }
  }

  const changeStatusFilterValue = useCallback(async (newValue) => {
    setStatusFilterValue(newValue);
    pageSizes = ITEMS_PER_PAGE
    page_ = INIT_PAGE
    currentPage = INIT_PAGE
    const response = await loadVersionData(apiUrl, bundleGroupId, page_, pageSizes, newValue);
    if (response && response.versions) {
      response.versions.payload && setBgVersionList(response.versions.payload)
      setTotalItems(response.versions.metadata.totalItems)
    }
  },[apiUrl, bundleGroupId])

  useEffect(() => {
    const getVersionList = async () => {
      const data = await loadVersionData(apiUrl, bundleGroupId, page_, ITEMS_PER_PAGE);
      if (!data || !data.versions || !data.versions.payload.length) {
        history.push("/")
        return
      }
      setBgVersionList(data.versions.payload);
      data.versions.payload && data.versions.payload[0].name && setBundleName(data.versions.payload[0].name)
      setLoading(false);
    }
    getVersionList();
  }, [apiUrl, reloadToken, bundleGroupId, history]);

  useEffect(() => {
    const getCategories = async () => {
      const data = (await getAllCategories(apiUrl));
      if (data.isError) {
        setLoading(false)
      }
      setCategories(data.categoryList);
    }
    getCategories();

    let unmounted = false;
    const setOrg = async () => {
      const orgData = await getAllOrganisations(apiUrl)
      orgData && orgData.organisationList && !unmounted && setOrgList(orgData.organisationList)
    }
    setOrg();
    return () => unmounted = true
  }, [apiUrl, reloadToken])

  const searchTermHandler = async (e) => {
    // search data on enter click
    if (e.keyCode === 13 && e.nativeEvent.srcElement) {
      setVersionSearchTerm(e.nativeEvent.srcElement.value)
      history.push('/')
    }
  }

  const onClearHandler = (e) => {
    // on clear cross icon clean the version search team
    if (e.type === 'click') setVersionSearchTerm('')
  }

  return (
    <>
      <Content className="CatalogPage">
        <div className="CatalogPage-wrapper">
          <div className="bx--grid bx--grid--full-width catalog-page">
            <div className="bx--row">
              <div className="bx--col-lg-12 CatalogPage-breadcrumb">
                <EhBreadcrumb pathElements={[{
                  path: bgVersionList && bundleName ? `${bundleName} ${i18n.t("component.button.viewVersions")}` : "",
                  href: window.location.href
                }]} />
              </div>
            </div>

            <div className="bx--row">
                <div className="bx--col-lg-4 CatalogPage-section">
                    {i18n.t('page.catalogPanel.catalogHomePage.categories')}
                </div>
              <div className="bx--col-lg-5 CatalogPage-section">
              {i18n.t('page.catalogPanel.catalogHomePage.catalog')}
              </div>
              <div className="bx--col-lg-3 CatalogPage-section">
                <Button renderIcon={Add16} disabled={true}>{i18n.t('component.button.add')}</Button>
              </div>
              <div className="bx--col-lg-4 CatalogPage-section">
                <Search placeholder={i18n.t('component.bundleModalFields.search')} onKeyDown={searchTermHandler} onChange={onClearHandler} labelText={'Search'} size="xl" id="search-1" />
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-4 CatalogPage-section">
              </div>
              <div className="bx--col-lg-12 CatalogPage-section">
                <BundleGroupStatusFilter onFilterValueChange={changeStatusFilterValue} />
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-4">
                {categories && categories.length > 0 &&
                  <CatalogFilterTile categories={categories} categoryId={categoryId}/>}
              </div>
              <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                {bgVersionList && bgVersionList.length
                  ?
                  <CatalogTiles bundleGroups={bgVersionList} isVersionsPage={IS_VERSIONS_PAGE} categoryDetails={categories}
                  orgList={orgList}
                  reloadToken={reloadToken} onAfterSubmit={onAfterSubmit} showFullPage={true} />
                  :
                  <div> {i18n.t('page.catalogPanel.noVersionsFound')} </div>}
                <Pagination
                  itemsPerPageText={i18n.t("component.pagination.itemsPerPage")}
                  itemRangeText={
                    (min, max, total) => `${min}–${max} ${i18n.t("component.pagination.of")} ${total} ${i18n.t("component.pagination.items")}`
                  }
                  page={currentPage}
                  pageSizes={[12, 18, 24]}
                  totalItems={totalItems}
                  onChange={onPaginationChange}
                  backwardText={i18n.t("component.pagination.previousPage")}
                  forwardText={i18n.t("component.pagination.nextPage")}
                  pageRangeText={
                    (total) => `${i18n.t("component.pagination.of")} ${total}
                        ${total === 1 ? `${i18n.t("component.pagination.page")}` : `${i18n.t("component.pagination.pages")}`}`
                  }
                />
              </div>
              </div>
          </div>
        </div>
      </Content>
      {loading && <Loading />}
    </>
  );
}

export default BundleGroupVersionsPage;
